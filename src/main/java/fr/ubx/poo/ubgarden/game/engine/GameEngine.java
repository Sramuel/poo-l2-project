    /*
     * Copyright (c) 2020. Laurent Réveillère
     */

    package fr.ubx.poo.ubgarden.game.engine;

    import fr.ubx.poo.ubgarden.game.*;
    import fr.ubx.poo.ubgarden.game.Map;
    import fr.ubx.poo.ubgarden.game.go.bonus.Bonus;
    import fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
    import fr.ubx.poo.ubgarden.game.go.decor.Decor;
    import fr.ubx.poo.ubgarden.game.go.decor.DoorNext;
    import fr.ubx.poo.ubgarden.game.go.decor.DoorPrevOpened;
    import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
    import fr.ubx.poo.ubgarden.game.go.personage.Wasp;
    import fr.ubx.poo.ubgarden.game.view.*;
    import javafx.animation.AnimationTimer;
    import javafx.application.Platform;
    import javafx.scene.Group;
    import javafx.scene.Scene;
    import javafx.scene.layout.Pane;
    import javafx.scene.layout.StackPane;
    import javafx.scene.paint.Color;
    import javafx.scene.text.Font;
    import javafx.scene.text.Text;
    import javafx.scene.text.TextAlignment;

    import java.util.*;
    import static fr.ubx.poo.ubgarden.game.view.ImageResource.INSECTICIDE;


    public final class GameEngine {

        private static AnimationTimer gameLoop;
        private final Game game;
        private final Gardener gardener;
        private final List<Sprite> sprites = new LinkedList<>();
        private final Set<Sprite> cleanUpSprites = new HashSet<>();

        private final Scene scene;

        private StatusBar statusBar;

        private final Pane rootPane = new Pane();
        private final Group root = new Group();
        private final Pane layer = new Pane();
        private Input input;

        public GameEngine(Game game, Scene scene) {
            this.game = game;
            this.scene = scene;
            this.gardener = game.getGardener();
            initialize();
            buildAndSetGameLoop();
        }

        public Pane getRoot() {
            return rootPane;
        }

        private void initialize() {
            int height = game.world().getGrid().height();
            int width = game.world().getGrid().width();
            int sceneWidth = width * ImageResource.size;
            int sceneHeight = height * ImageResource.size;
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());
            input = new Input(scene);

            root.getChildren().clear();
            root.getChildren().add(layer);
            statusBar = new StatusBar(root, sceneWidth, sceneHeight);

            rootPane.getChildren().clear();
            rootPane.setPrefSize(sceneWidth, sceneHeight + StatusBar.height);
            rootPane.getChildren().add(root);

            // Create sprites
            int currentLevel = game.world().currentLevel();

            for (var decor : game.world().getGrid().values()) {
                sprites.add(SpriteFactory.create(layer, decor));
                decor.setModified(true);
                var bonus = decor.getBonus();
                if (bonus != null) {
                    sprites.add(SpriteFactory.create(layer, bonus));
                    bonus.setModified(true);
                }
            }

            sprites.add(new SpriteGardener(layer, gardener));
            resizeScene(sceneWidth, sceneHeight);
        }

        void buildAndSetGameLoop() {
            gameLoop = new AnimationTimer() {
                public void handle(long now) {
                    checkLevel();

                    // Check keyboard actions
                    processInput();

                    // Do actions
                    update(now);
                    checkCollision();

                    // Graphic update
                    cleanupSprites();
                    render();
                    statusBar.update(game);
                }
            };
        }


        private void checkLevel() {
            if (game.isSwitchLevelRequested()) {
                //nettoyage de l'ancien niveau
                layer.getChildren().clear();
                sprites.clear();
                int levelAvant = game.world().currentLevel(); // On mémorise le niveau précédent avant le switch
                //transition vers le nouveau niveau
                game.switchLevel();
                initialize();
                //repositionnement du jardinier qui doit être placé sur la porte d'entrée du nouveau niveau
                Position newPos = findDoorPosition(levelAvant);
                game.getGardener().setPosition(newPos);
            }
        }

        private Position findDoorPosition(int levelAvant) {
            int targetLevel = game.getSwitchLevel();
            Map currentMap = game.world().getGrid(); //la carte du nouveau niveau

            for (int x = 0; x < currentMap.width(); x++) {
                for (int y = 0; y < currentMap.height(); y++) {
                    Position pos = new Position(targetLevel, x, y);
                    Decor decor = currentMap.get(pos);

                    //si on monte, on cherche la porte d'entrée du nouveau niveau
                    if (targetLevel > levelAvant && decor instanceof DoorPrevOpened) {
                        return pos;
                    }
                    // Si on descend, on cherche la porte de sortie de l'ancien niveau
                    if (targetLevel < levelAvant && decor instanceof DoorNext) {
                        return pos;
                    }
                }
            }
            return game.getGardener().getPosition();
        }

        private void checkCollision() {
            if (game.world().getGrid() instanceof Level level) {
                //on parcourt la liste à l'envers pour pouvoir supprimer sans sauter un elt
                for (int i = level.getWasps().size()-1; i >= 0; i--) {
                    Wasp wasp = level.getWasps().get(i);
                    //si la guêpe touche le jardinier
                    if (gardener.getPosition().equals(wasp.getPosition())) {
                        gardener.hurt(20);  //-20 énergie
                        wasp.remove();              //la guêpe meurt
                        level.getWasps().remove(i); //on la retire de la liste
                    }
                }
            }
        }

        private void processInput() {
            if (input.isExit()) {
                gameLoop.stop();
                Platform.exit();
                System.exit(0);
            } else if (input.isMoveDown()) {
                gardener.requestMove(Direction.DOWN);
            } else if (input.isMoveLeft()) {
                gardener.requestMove(Direction.LEFT);
            } else if (input.isMoveRight()) {
                gardener.requestMove(Direction.RIGHT);
            } else if (input.isMoveUp()) {
                gardener.requestMove(Direction.UP);
            }
            input.clear();
        }

        private void showMessage(String msg, Color color) {
            Text message = new Text(msg);
            message.setTextAlignment(TextAlignment.CENTER);
            message.setFont(new Font(60));
            message.setFill(color);

            StackPane pane = new StackPane(message);
            pane.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
            rootPane.getChildren().clear();
            rootPane.getChildren().add(pane);

            new AnimationTimer() {
                public void handle(long now) {
                    processInput();
                }
            }.start();
        }

        private void update(long now) {
            game.world().getGrid().values().forEach(decor -> decor.update(now));

            if (game.world().getGrid() instanceof Level level) {
                for (Wasp wasp : level.getWasps()) {
                    wasp.update(now);
                    //si la guêpe vient de naître et n'a pas d'image alors on lui en crée une
                    boolean hasSprite = false;
                    for (Sprite s : sprites) {
                        if (s.getGameObject() == wasp) {
                            hasSprite = true;
                            break; //on a trouvé alors on arrête de chercher
                        }
                    }
                    if (!hasSprite) {
                        sprites.add(new SpriteWasp(layer, wasp));
                    }
                }
                for (Decor decor : game.world().getGrid().values()) {
                    Bonus bonus = decor.getBonus();
                    //si l'insecticide vient d'apparaitre et n'a pas d'image alors on lui en crée une
                    if (bonus instanceof Insecticide insecticide) {
                        boolean hasSprite = false;
                        for (Sprite s : sprites) {
                            if (s.getGameObject() == insecticide) {
                                hasSprite = true;
                                break;
                            }
                        }
                        if (!hasSprite) {
                            sprites.add(new Sprite(layer, ImageResourceFactory.getInstance().get(INSECTICIDE), insecticide));
                        }
                    }
                }
            }

            gardener.update(now);

            if (gardener.getEnergy() <= 0) {
                gameLoop.stop();
                showMessage("Perdu!", Color.RED);
            }
            if (gardener.getWin()) {
                gameLoop.stop();
                showMessage("Gagné!", Color.GOLD);
            }
        }

        public void cleanupSprites() {
            sprites.forEach(sprite -> {
                if (sprite.getGameObject().isDeleted()) {
                    cleanUpSprites.add(sprite);
                }
            });
            cleanUpSprites.forEach(Sprite::remove);
            sprites.removeAll(cleanUpSprites);
            cleanUpSprites.clear();
        }

        private void render() {
            sprites.forEach(sprite -> {
                sprite.updateImage(); //mise à jour du sprite de la porte à ouvrir
                sprite.render();
            });
        }

        public void start() {
            gameLoop.start();
        }

        private void resizeScene(int width, int height) {
            rootPane.setPrefSize(width, height + StatusBar.height);
            layer.setPrefSize(width, height);
            Platform.runLater(() -> scene.getWindow().sizeToScene());
        }
    }