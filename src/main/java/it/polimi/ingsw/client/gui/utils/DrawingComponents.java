package it.polimi.ingsw.client.gui.utils;

import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.server.game_state.CharacterState;
import it.polimi.ingsw.server.game_state.CloudState;
import it.polimi.ingsw.server.game_state.GameState;
import it.polimi.ingsw.server.game_state.PlayerState;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Optional;

public class DrawingComponents {

    public static void drawFourPlayersGame(GameState gameState, double width, double height, AnchorPane root, String username) {
        List<PlayerState> players = gameState.getPlayers();
        drawGameComponentsForTwo(width, height, root, gameState, username);
        drawDashboard(players.get(2), 0, height - (width * (1454.0 / 3352.0) * 0.4), width * 0.4, root);
        double textStartingY = height - (width * (1454.0 / 3352.0) * 0.4) - height / 45;
        drawDashboardText(players.get(2), 0, textStartingY, root, width, height);
        drawDashboard(players.get(3), width * 0.6, height - (width * (1454.0 / 3352.0) * 0.4), width * 0.4, root);
        drawDashboardText(players.get(3), width * 0.6, textStartingY, root, width, height);
    }

    public static void drawThreePlayersGame(GameState gameState, double width, double height, AnchorPane root, String username) {
        List<PlayerState> players = gameState.getPlayers();
        drawGameComponentsForTwo(width, height, root, gameState, username);
        drawDashboard(players.get(2), 0, height - (width * (1454.0 / 3352.0) * 0.4), width * 0.4, root);
        drawDashboardText(players.get(2), 0, height - (width * (1454.0 / 3352.0) * 0.4) - height / 45, root, width, height);
    }

    public static void drawTwoPlayersGame(GameState gameState, double width, double height, AnchorPane root, String username) {
        drawGameComponentsForTwo(width, height, root, gameState, username);
    }

    private static void drawGameComponentsForTwo(double width, double height, AnchorPane root, GameState gameState, String username) {
        List<PlayerState> players = gameState.getPlayers();
        drawDashboard(players.get(0), 0, 0, width * 0.4, root);
        double textStartingY = width * (1454.0 / 3352.0) * 0.4 + height / 30;
        drawDashboardText(players.get(0), 0, textStartingY, root, width, height);
        drawDashboard(players.get(1), width * 0.6, 0, width * 0.4, root);
        drawDashboardText(players.get(1), width * 0.6, textStartingY, root, width, height);

        drawClouds(gameState.getClouds(), width, height, root);
        // Draw all three characters
        if (gameState.isExpert()) {
            drawCharacters(gameState.getCharacters(), width, height, root);
        }
        PlayerState player = gameState.getPlayers().stream().filter(p -> p.getName().equals(username)).findAny().orElse(null);
        if (player != null) {
            drawAssistants(player.getAssistants(), width, height);
        }
    }

    private static void drawClouds(List<CloudState> clouds, double width, double height, AnchorPane root) {
        // Draw the first two clouds on the left side of the screen and the remaining ones - if present - on the right
        root.getChildren().add(getCloudsGroup(clouds.subList(0, 2), width, height, false));
        root.getChildren().add(getCloudsGroup(clouds.subList(2, clouds.size()), width, height, true));
    }

    private static VBox getCloudsGroup(List<CloudState> clouds, double width, double height, boolean isOnRightSide) {
        VBox cloudsBox = new VBox();
        double layoutX;
        if (isOnRightSide) {
            layoutX = width - height * DrawingConstants.CLOUD_HEIGHT - width * DrawingConstants.OFFSET_OF_CLOUD_FROM_BORDER;
        } else {
            layoutX = width * DrawingConstants.OFFSET_OF_CLOUD_FROM_BORDER;
        }
        cloudsBox.setLayoutY(height * DrawingConstants.CLOUD_STARTING_HEIGHT);
        cloudsBox.setLayoutX(layoutX);
        cloudsBox.setSpacing(height * DrawingConstants.SPACE_BETWEEN_CLOUDS);

        for (int i = 0; i < Math.min(2, clouds.size()); i++) {
            CloudState cloud = clouds.get(i);
            AnchorPane cloudToDraw = getCloudWithStudents(cloud, width, height);
            cloudsBox.getChildren().add(cloudToDraw);
        }

        return cloudsBox;
    }

    private static AnchorPane getCloudWithStudents(CloudState cloud, double width, double height) {
        ImageView cloudBackground = new ImageView(new Image("/gameboard/clouds/cloud_card.png"));
        cloudBackground.setPreserveRatio(true);
        cloudBackground.setFitHeight(height * DrawingConstants.CLOUD_HEIGHT);

        GridPane studentsPane = new GridPane();
        Bounds imageBounds = cloudBackground.boundsInParentProperty().get();
        studentsPane.setLayoutX(imageBounds.getWidth() * DrawingConstants.OFFSET_OF_STUDENT_FROM_CLOUD);
        studentsPane.setLayoutY(imageBounds.getHeight() * DrawingConstants.OFFSET_OF_STUDENT_FROM_CLOUD);
        studentsPane.setHgap(imageBounds.getWidth() * DrawingConstants.OFFSET_BETWEEN_STUDENTS_IN_CLOUD);
        studentsPane.setVgap(imageBounds.getHeight() * DrawingConstants.OFFSET_BETWEEN_STUDENTS_IN_CLOUD);

        if (cloud.getStudents() != null) {
            for (int j = 0; j < cloud.getStudents().size(); j++) {
                String studentPath = "/gameboard/students/student_" +
                        cloud.getStudents().get(j).getColor().toString().toLowerCase() + ".png";
                ImageView student = new ImageView(new Image(studentPath));
                student.setPreserveRatio(true);
                student.setFitWidth(width * 0.4 / 25);

                studentsPane.add(student, j % 2, j / 2);
            }
        }

        return new AnchorPane(cloudBackground, studentsPane);
    }

    private static void drawCharacters(List<CharacterState> characters, double width, double height, AnchorPane root) {
        double coordX = width * DrawingConstants.CHARACTERS_BEGINNING_PROPORTION;
        int heightProportion = 13;

        for (CharacterState character : characters) {
            String imagePath = "/gameboard/characters/" + character.getCharacterName() + ".jpg";
            ImageView characterImage = new ImageView(new Image(imagePath));
            characterImage.setX(coordX);
            characterImage.setY(height / heightProportion);
            characterImage.setPreserveRatio(true);
            characterImage.setFitWidth(width * DrawingConstants.CHARACTER_CARD_PROPORTION);

            root.getChildren().add(characterImage);

            if (character.isHasCoin()) {
                double imageWidth = width * (DrawingConstants.COIN_PROPORTION);
                ImageView coin = getImageView(
                        "/gameboard/Moneta_base.png",
                        coordX + width * (DrawingConstants.CHARACTER_CARD_PROPORTION) - imageWidth,
                        height / 20,
                        imageWidth
                );
                root.getChildren().add(coin);
            }
            if (character.getStudents() != null) {
                GridPane grid = new GridPane();
                grid.setLayoutX(coordX + width * DrawingConstants.SPACE_BETWEEN_STUDENTS_ON_CHARACTERS);
                grid.setLayoutY(height / heightProportion + characterImage.boundsInParentProperty().get().getHeight());

                for (int i = 0; i < character.getStudents().size(); i++) {
                    String studentPath = "/gameboard/students/student_" +
                            character.getStudents().get(i).getColor().toString().toLowerCase() + ".png";
                    ImageView student = new ImageView(new Image(studentPath));
                    student.setPreserveRatio(true);
                    student.setFitWidth(width * 0.4 / 25);

                    grid.add(student, i / 2, i % 2);
                }

                root.getChildren().add(grid);
            }

            coordX += width * (DrawingConstants.CHARACTER_CARD_PROPORTION + DrawingConstants.SPACE_BETWEEN_CHARACTERS_PROPORTION);
        }
    }

    private static void drawAssistants(int[] assistants, double width, double height) {

    }

    private static void drawDashboard(PlayerState player, double x, double y, double width, AnchorPane root) {
        ImageView dashboard = new ImageView(new Image("/gameboard/Plancia_DEF_circles.png"));
        dashboard.setPreserveRatio(true);
        dashboard.setFitWidth(width);
        dashboard.setX(x);
        dashboard.setY(y);
        root.getChildren().add(dashboard);

        double coordX, coordY;
        // Add students to entrance
        for (int i = 0; i < player.getEntrance().size(); i++) {
            Student s = player.getEntrance().get(i);
            String resourceName = "/gameboard/students/student_" + s.getColor().toString().toLowerCase() + ".png";
            ImageView student = new ImageView(new Image(resourceName));
            student.setPreserveRatio(true);
            student.setFitWidth(width / 25);

            if (i % 2 == 0) {
                coordX = x + width / 10.7;
            } else {
                coordX = x + width / 29.412;
            }
            coordY = y + (width / 18.182) + (width / 13.793) * (i / 2 + i % 2);

            student.setX(coordX);
            student.setY(coordY);
            root.getChildren().add(student);
        }
        // Add students to dining room
        for (int i = 0; i < player.getDining().size(); i++) {
            Student s = player.getDining().get(i);
            String resourceName = "/gameboard/students/student_" + s.getColor().toString().toLowerCase() + ".png";
            ImageView student = new ImageView(new Image(resourceName));
            student.setPreserveRatio(true);
            student.setFitWidth(width / 25);

            coordY = y + width / 18.182;
            double deltaY = width / 13.793;
            switch (s.getColor()) {
                case RED -> coordY += deltaY;
                case YELLOW -> coordY += 2 * deltaY;
                case PINK -> coordY += 3 * deltaY;
                case BLUE -> coordY += 4 * deltaY;
            }

            int multiplyingNumber = 0;
            for (int j = 0; j < i; j++) {
                if (player.getDining().get(j).getColor() == s.getColor()) {
                    multiplyingNumber++;
                }
            }
            coordX = x + width / 5.33 + (width / 21.05) * multiplyingNumber;

            student.setX(coordX);
            student.setY(coordY);
            root.getChildren().add(student);
        }
        // Add professors
        for (Color color : player.getOwnedProfessors()) {
            String path = "/gameboard/professors/teacher_" + color.toString().toLowerCase() + ".png";
            ImageView professor = new ImageView(new Image(path));
            professor.setPreserveRatio(true);
            professor.setFitWidth(width / 20);
            professor.setRotate(90);

            coordX = x + width / 1.4184;
            coordY = y + width / 20;
            double deltaY = width / 14.035;
            switch (color) {
                case RED -> coordY += deltaY;
                case YELLOW -> coordY += 2 * deltaY;
                case PINK -> coordY += 3 * deltaY;
                case BLUE -> coordY += 4 * deltaY;
            }

            professor.setX(coordX);
            professor.setY(coordY);
            root.getChildren().add(professor);
        }
        // Add towers
        for (int i = 0; i < player.getRemainingTowers(); i++) {
            String path = "/gameboard/towers/" + player.getTowerColor().toString().toLowerCase() + "_tower.png";
            ImageView tower = new ImageView(new Image(path));
            tower.setPreserveRatio(true);
            tower.setFitWidth(width / 15.385);

            if (i % 2 == 0) {
                coordX = x + width / 1.242;
            } else {
                coordX = x + width / 1.133;
            }
            coordY = y + width / 13.333 + (i / 2) * (width / 13.793);

            tower.setX(coordX);
            tower.setY(coordY);
            root.getChildren().add(tower);
        }

    }

    private static void drawDashboardText(PlayerState player, double x, double y, AnchorPane root, double width, double height) {
        double startingX = x + width * DrawingConstants.PLAYER_NAME_INITIAL_PADDING;

        Text text = new Text(player.getName() + " | " + player.getNumCoins() + "x");
        text.setX(startingX);
        text.setY(y);
        text.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.BOLD, DrawingConstants.TITLE_FONT_SIZE));
        root.getChildren().add(text);
        ImageView coin = getImageView("/gameboard/Moneta_base.png", startingX + text.getLayoutBounds().getWidth(),
                y - height / 24, width * DrawingConstants.COIN_PROPORTION);
        root.getChildren().add(coin);
    }

    private static ImageView getImageView(String path, double x, double y, double fitWidth) {
        ImageView iv = new ImageView(new Image(path));
        iv.setPreserveRatio(true);
        iv.setFitWidth(fitWidth);
        iv.setX(x);
        iv.setY(y);
        return iv;
    }
}
