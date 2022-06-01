package it.polimi.ingsw.client.gui.utils;

import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.server.game_state.*;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DrawingComponents {

    private static double dashboardHeight;
    private static final List<BorderPane> characterImages = new ArrayList<>();
    private static final List<AnchorPane> cloudImages = new ArrayList<>();
    private static final List<BorderPane> assistantCards = new ArrayList<>();
    private static final List<BorderPane> entranceStudents = new ArrayList<>();
    private static final List<BorderPane> diningGaps = new ArrayList<>();
    private static final List<BorderPane> diningStudents = new ArrayList<>();
    private static final HashMap<CharacterName, List<BorderPane>> studentsOnCharacter = new HashMap<>();
    public static final HashMap<Integer, List<BorderPane>> studentsOnIslands = new HashMap<>();
    public static final List<BorderPane> noEntriesOnIslands = new ArrayList<>();
    private static final List<BorderPane> islands = new ArrayList<>();

    public static void drawTwoPlayersGame(GameState gameState, double pageWidth, double pageHeight, AnchorPane root, String username) {
        drawGameComponentsForTwo(pageWidth, pageHeight, root, gameState, username);

        drawAssistants(gameState, pageWidth, pageHeight, root, username);
    }

    public static void drawThreePlayersGame(GameState gameState, double pageWidth, double pageHeight, AnchorPane root, String username) {
        List<PlayerState> players = gameState.getPlayers();
        drawGameComponentsForTwo(pageWidth, pageHeight, root, gameState, username);

        drawDashboard(players.get(2), pageHeight * 0.5, root, username);
        drawDashboardText(players.get(2), 0 - pageWidth * 0.03, 3 * dashboardHeight + pageHeight * 0.095, pageWidth, pageHeight, root, gameState.isExpert());

        drawAssistants(gameState, pageWidth, pageHeight, root, username);
    }

    public static void drawFourPlayersGame(GameState gameState, double pageWidth, double pageHeight, AnchorPane root, String username) {
        List<PlayerState> players = gameState.getPlayers();
        drawGameComponentsForTwo(pageWidth, pageHeight, root, gameState, username);

        drawDashboard(players.get(2), pageHeight * 0.50, root, username);
        drawDashboardText(players.get(2), 0 - pageWidth * 0.03, 3 * dashboardHeight + pageHeight * 0.095, pageWidth, pageHeight, root, gameState.isExpert());

        drawDashboard(players.get(3), pageHeight * 0.78, root, username);
        drawDashboardText(players.get(3), dashboardHeight / (2 * DrawingConstants.DASHBOARD_HEIGHT_OVER_WIDTH) - pageWidth * 0.03, 3 * dashboardHeight + pageHeight * 0.095, pageWidth, pageHeight, root, gameState.isExpert());

        drawAssistants(gameState, pageWidth, pageHeight, root, username);
    }

    private static void drawGameComponentsForTwo(double pageWidth, double pageHeight, AnchorPane root, GameState gameState, String username) {
        root.getStylesheets().add("/css/game_elements.css");

        dashboardHeight = pageHeight * 0.22;
        List<PlayerState> players = gameState.getPlayers();

        drawDashboard(players.get(0), 0, root, username);
        drawDashboardText(players.get(0), 0 - pageWidth * 0.03, dashboardHeight + pageHeight * 0.035, pageWidth, pageHeight, root, gameState.isExpert());

        drawDashboard(players.get(1), pageHeight * 0.28, root, username);
        drawDashboardText(players.get(1), dashboardHeight / (2 * DrawingConstants.DASHBOARD_HEIGHT_OVER_WIDTH) - pageWidth * 0.03, dashboardHeight + pageHeight * 0.035, pageWidth, pageHeight, root, gameState.isExpert());

        drawClouds(gameState.getClouds(), pageWidth, pageHeight, root);
        drawIslands(gameState, pageWidth, pageHeight, root);
        // Draw all three characters
        if (gameState.isExpert()) {
            drawCharacters(gameState.getCharacters(), pageWidth, pageHeight, root);
        }
    }

    private static void drawDashboard(PlayerState player, double y, AnchorPane root, String username) {
        ImageView dashboardImage = new ImageView(new Image("/gameboard/Plancia_DEF_circles.png"));
        dashboardImage.setPreserveRatio(true);
        dashboardImage.setFitHeight(dashboardHeight);
        double dashboardWidth = dashboardImage.getBoundsInParent().getWidth();
        BorderPane dashboard = new BorderPane(dashboardImage);
        dashboard.setLayoutX(0);
        dashboard.setLayoutY(y);
        root.getChildren().add(dashboard);

        // Add students to entrance
        GridPane newEntrance = getGridPane(
                dashboardWidth * DrawingConstants.INITIAL_X_OFFSET_ENTRANCE, dashboardWidth * DrawingConstants.INITIAL_Y_OFFSET_ENTRANCE,
                dashboardWidth * DrawingConstants.ENTRANCE_HGAP, dashboardWidth * DrawingConstants.ENTRANCE_VGAP
        );
        for (int i = 0; i < player.getEntrance().size(); i++) {
            Student s = player.getEntrance().get(i);
            String resourceName = "/gameboard/students/student_" + s.getColor().toString().toLowerCase() + ".png";
            ImageView student = new ImageView(new Image(resourceName));
            student.setPreserveRatio(true);
            student.setFitWidth(dashboardWidth / 25);
            student.setOnMouseClicked(mouseEvent -> ObjectClickListeners.setStudentClicked(s.getColor(), student));

            BorderPane studentPane = new BorderPane(student);
            if (username.equals(player.getName())) {
                studentPane.setOnMouseClicked(event -> ObjectClickListeners.setStudentClicked(s.getColor(), studentPane));
                entranceStudents.add(studentPane);
            }

            newEntrance.add(studentPane, (i + 1) % 2, i / 2 + i % 2);
        }
        // Students in entrance are attached to the gameboard and not to the root directly
        dashboard.getChildren().add(newEntrance);

        // Add students to dining room
        GridPane newDiningRoom = getGridPane(dashboardWidth * DrawingConstants.INITIAL_X_OFFSET_DINING_ROOM,
                dashboardWidth * DrawingConstants.INITIAL_Y_OFFSET_DINING_ROOM, dashboardWidth * DrawingConstants.DINING_ROOM_HGAP,
                dashboardWidth * DrawingConstants.DINING_ROOM_VGAP);

        List<Color> colorsInOrder = List.of(Color.GREEN, Color.RED, Color.YELLOW, Color.PINK, Color.BLUE);
        HashMap<Color, Integer> diningStudents = new HashMap<>();
        for (int i = 0; i < player.getDining().size(); i++) {
            Student s = player.getDining().get(i);
            String resourceName = "/gameboard/students/student_" + s.getColor().toString().toLowerCase() + ".png";
            ImageView student = new ImageView(new Image(resourceName));
            student.setPreserveRatio(true);
            student.setFitWidth(dashboardWidth / 25);

            int row = colorsInOrder.indexOf(s.getColor());
            Integer column = diningStudents.get(s.getColor());
            if (column == null) {
                column = 0;
            }

            BorderPane studentWithBorder = new BorderPane(student);
            studentWithBorder.setOnMouseClicked(event -> ObjectClickListeners.setStudentOnDiningClicked(s.getColor(), studentWithBorder));
            diningStudents.put(s.getColor(), column + 1);
            newDiningRoom.add(studentWithBorder, column, row);

            if (username.equals(player.getName())) {
                DrawingComponents.diningStudents.add(studentWithBorder);
            }
        }

        for (Color color : colorsInOrder) {
            diningStudents.putIfAbsent(color, 0);
            int positionOfEmptySpace = diningStudents.get(color);
            if (positionOfEmptySpace == 10) break;

            // If you want to change to a circle, the radius is width / 25 * 0,55
            BorderPane emptySpace = new BorderPane();
            emptySpace.setOnMouseClicked(mouseEvent -> ObjectClickListeners.setDiningRoomClicked());
            emptySpace.setMaxSize(dashboardWidth / 25, dashboardWidth / 25);
            emptySpace.setMinSize(dashboardWidth / 25, dashboardWidth / 25);

            newDiningRoom.add(emptySpace, positionOfEmptySpace, colorsInOrder.indexOf(color));

            if (username.equals(player.getName())) {
                diningGaps.add(emptySpace);
            }
        }
        // Students in dining room are attached to the dashboard and not to the root directly
        dashboard.getChildren().add(newDiningRoom);

        // Add professors
        GridPane professorRoom = getGridPane(
                dashboardWidth * DrawingConstants.INITIAL_X_OFFSET_PROFESSOR_ROOM, dashboardWidth * DrawingConstants.INITIAL_Y_OFFSET_PROFESSOR_ROOM,
                0, dashboardWidth * DrawingConstants.PROFESSOR_ROOM_VGAP
        );
        for (int i = 0; i < colorsInOrder.size(); i++) {
            Color color = colorsInOrder.get(i);
            if (player.getOwnedProfessors().contains(color)) {
                String path = "/gameboard/professors/teacher_" + color.toString().toLowerCase() + ".png";
                ImageView professor = new ImageView(new Image(path));
                professor.setPreserveRatio(true);
                professor.setFitWidth(dashboardWidth / 20);
                professor.setRotate(90);

                professorRoom.add(professor, 0, i);
            } else {
                Pane emptyPlace = new Pane();
                emptyPlace.setMaxSize(dashboardWidth / 20, dashboardWidth / 20);
                emptyPlace.setMinSize(dashboardWidth / 20, dashboardWidth / 20);
                professorRoom.add(emptyPlace, 0, i);
            }

        }
        // Professors are attached to the gameboard and not to the root directly
        dashboard.getChildren().add(professorRoom);

        // Add towers
        GridPane towers = getGridPane(
                dashboardWidth * DrawingConstants.INITIAL_X_OFFSET_TOWERS, dashboardWidth * DrawingConstants.INITIAL_Y_OFFSET_TOWERS,
                dashboardWidth * DrawingConstants.TOWERS_HGAP, dashboardWidth * DrawingConstants.TOWERS_VGAP
        );
        for (int i = 0; i < player.getRemainingTowers(); i++) {
            String path = "/gameboard/towers/" + player.getTowerColor().toString().toLowerCase() + "_tower.png";
            ImageView tower = new ImageView(new Image(path));
            tower.setPreserveRatio(true);
            tower.setFitWidth(dashboardWidth * DrawingConstants.TOWERS_SIZE);

            towers.add(tower, i % 2, i / 2);
        }
        // Towers are attached to the gameboard and not to the root directly
        dashboard.getChildren().add(towers);

    }

    private static void drawDashboardText(PlayerState player, double x, double y, double pageWidth, double pageHeight, AnchorPane root, boolean isExpert) {
        double startingX = x + pageWidth * DrawingConstants.PLAYER_NAME_INITIAL_PADDING;
        Text text;

        if (!isExpert) {
            if (x == 0 - pageWidth * 0.03) {
                text = new Text("↑ | " + player.getName());
            } else {
                text = new Text("↓ | " + player.getName());
            }
            text.setX(startingX);
            text.setY(y);
            text.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.BOLD, DrawingConstants.TITLE_FONT_SIZE));
            root.getChildren().add(text);
        } else {
            if (x == 0 - pageWidth * 0.03) {
                text = new Text("↑ | " + player.getName() + " | " + player.getNumCoins() + "x");
            } else {
                text = new Text("↓ | " + player.getName() + " | " + player.getNumCoins() + "x");
            }
            text.setX(startingX);
            text.setY(y);
            text.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.BOLD, DrawingConstants.TITLE_FONT_SIZE));
            root.getChildren().add(text);
            ImageView coin = getImageView("/gameboard/Moneta_base.png", startingX + text.getLayoutBounds().getWidth(),
                    y - pageHeight / 24, pageWidth * DrawingConstants.COIN_PROPORTION);
            root.getChildren().add(coin);
        }
    }

    private static void drawIslands(GameState gameState, double width, double height, AnchorPane root) {
        List<IslandState> islands = gameState.getIslands();
        double deltaAngle = (2 * 3.14) / islands.size();
        double radius = height * 0.32;
        for (int i = 0; i < islands.size(); i++) {
            String path = "/gameboard/islands/Isola_" + ((i % 3) + 1) + ".png";
            ImageView island = new ImageView(path);
            island.setPreserveRatio(true);
            island.setFitWidth(width * 0.1);
            Bounds imageBounds = island.boundsInParentProperty().get();
            double islandWidth = imageBounds.getWidth();
            double islandHeight = imageBounds.getHeight();
            BorderPane bp = new BorderPane(island);
            int steps = (i - gameState.getMNIndex() + gameState.getIslands().size()) % gameState.getIslands().size();
            int islandIndex = i;
            bp.setOnMouseClicked(event -> ObjectClickListeners.setIslandClicked(bp, steps, islandIndex));
            DrawingComponents.islands.add(bp);
            double X = Math.cos(deltaAngle * i) * radius;
            double Y = Math.sin(deltaAngle * i) * radius;
            double startingXIsland = width * 0.73 - islandWidth / 2 + X;
            double startingYIsland = height * 0.4 - islandHeight / 2 + Y;
            bp.setLayoutX(startingXIsland);
            bp.setLayoutY(startingYIsland);
            root.getChildren().add(bp);

            GridPane elementsOnIsland = new GridPane();
            elementsOnIsland.setLayoutX(width * 0.019);
            elementsOnIsland.setLayoutY(height * 0.025);

            for (int j = 0; j < islands.get(i).getNumOfTowers(); j++) {
                String towerPath = "/gameboard/towers/" + islands.get(i).getTowerColor().toString().toLowerCase() + "_tower.png";
                ImageView tower = new ImageView(new Image(towerPath));
                tower.setPreserveRatio(true);
                tower.setFitWidth(islandWidth / 4);
                elementsOnIsland.add(tower, j, 0);
            }

            int lastRow = 1;
            List<BorderPane> studentsOnIsland = new ArrayList<>();
            for (int j = 0; j < islands.get(i).getStudents().size(); j++) {
                Student s = islands.get(i).getStudents().get(j);
                String studentPath = "/gameboard/students/student_" + s.getColor().toString().toLowerCase() + ".png";
                ImageView student = new ImageView(new Image(studentPath));
                student.setPreserveRatio(true);
                student.setFitWidth(islandWidth / 8);
                lastRow = j / 4 + 1;
                BorderPane studentBorderPane = new BorderPane(student);
                studentBorderPane.setOnMouseClicked(event ->
                        ObjectClickListeners.setStudentOnIslandClicked(studentBorderPane, s.getColor(), islandIndex));
                studentsOnIsland.add(studentBorderPane);

                elementsOnIsland.add(studentBorderPane, j % 4, lastRow);
            }
            DrawingComponents.studentsOnIslands.put(i, studentsOnIsland);

            for (int j = 0; j < islands.get(i).getNoEntryNum(); j++) {
                ImageView noEntry = new ImageView(new Image("/gameboard/deny_island_icon.png"));
                noEntry.setPreserveRatio(true);
                noEntry.setFitWidth(islandWidth / 8);

                BorderPane noEntryBorderPane = new BorderPane(noEntry);
                noEntryBorderPane.setOnMouseClicked(event -> {
                }); // TODO remember to pass island index
                noEntriesOnIslands.add(noEntryBorderPane);
                elementsOnIsland.add(noEntryBorderPane, j % 4, lastRow + 1);
            }

            bp.getChildren().add(elementsOnIsland);

            if (i == gameState.getMNIndex()) {
                ImageView MN = new ImageView(new Image("/gameboard/mother_nature.png"));
                MN.setPreserveRatio(true);
                MN.setFitWidth(islandWidth * 0.3);
                BorderPane motherNature = new BorderPane(MN);
                motherNature.setLayoutX(islandWidth - width * 0.025);
                motherNature.setLayoutY(islandHeight / 3);
                bp.getChildren().add(motherNature);
            }
        }
    }

    private static void drawAssistants(GameState gameState, double pageWidth, double pageHeight, AnchorPane root, String username) {
        PlayerState player = gameState.getPlayers().stream().filter(p -> p.getName().equals(username)).findAny().orElse(null);
        if (player != null && player.getAssistants() != null) {
            double initialX = dashboardHeight / DrawingConstants.DASHBOARD_HEIGHT_OVER_WIDTH + pageWidth * DrawingConstants.OFFSET_OF_FIRST_ASSISTANT;
            double finalX = pageWidth * (1 - DrawingConstants.OFFSET_OF_FIRST_ASSISTANT);
            GridPane assistants = getAssistants(player.getAssistants(), pageWidth, pageHeight, initialX, finalX);
            root.getChildren().add(assistants);
        }
    }

    private static GridPane getAssistants(int[] assistants, double pageWidth, double pageHeight, double initialX, double finalX) {
        if (assistants.length == 0) return new GridPane();
        double initialY = pageHeight * 0.77;
        double spaceForAssistants = finalX - initialX - (assistants.length - 1) * pageWidth * DrawingConstants.OFFSET_BETWEEN_ASSISTANTS;
        GridPane gridPane = new GridPane();
        gridPane.setLayoutX(initialX);
        gridPane.setLayoutY(initialY);
        gridPane.setHgap(pageWidth * DrawingConstants.OFFSET_BETWEEN_ASSISTANTS);
        gridPane.setVgap(pageHeight * DrawingConstants.OFFSET_OF_FIRST_ASSISTANT);

        for (int value : assistants) {
            String path = "/gameboard/assistants/Assistente (" + value + ").png";
            ImageView assistant = new ImageView(new Image(path));
            assistant.setPreserveRatio(true);
            double minWidth = DrawingConstants.ASSISTANT_MAX_WIDTH_SINGLE_LINE;
            assistant.setFitWidth(spaceForAssistants * Math.min((double) 2 / assistants.length, minWidth));

            BorderPane assistantPane = new BorderPane(assistant);
            assistantPane.setOnMouseClicked(event -> ObjectClickListeners.setAssistantClicked(value, assistantPane));
            assistantCards.add(assistantPane);

            gridPane.add(assistantPane, value - 1, 0);
        }

        return gridPane;
    }


    private static void drawClouds(List<CloudState> clouds, double pageWidth, double pageHeight, AnchorPane root) {
        GridPane cloudGrid = new GridPane();
        double layoutX = dashboardHeight / DrawingConstants.DASHBOARD_HEIGHT_OVER_WIDTH + pageWidth * DrawingConstants.OFFSET_OF_CLOUD_FROM_BORDER;
        cloudGrid.setLayoutY(pageHeight * DrawingConstants.CLOUD_STARTING_HEIGHT);
        cloudGrid.setLayoutX(layoutX);
        cloudGrid.setHgap(pageHeight * DrawingConstants.SPACE_BETWEEN_CLOUDS);
        cloudGrid.setVgap(pageHeight * DrawingConstants.SPACE_BETWEEN_CLOUDS);

        for (int i = 0; i < clouds.size(); i++) {
            CloudState cloud = clouds.get(i);
            AnchorPane cloudToDraw = getCloudWithStudents(cloud, pageWidth, pageHeight);
            int cloudIndex = i;
            cloudToDraw.setOnMouseClicked(event -> ObjectClickListeners.setCloudClicked(cloudToDraw, cloudIndex));
            cloudGrid.add(cloudToDraw, i % 2, i / 2);
            cloudImages.add(cloudToDraw);
        }
        root.getChildren().add(cloudGrid);
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

    private static void drawCharacters(List<CharacterState> characters, double pageWidth, double pageHeight, AnchorPane root) {
        double coordX = dashboardHeight / DrawingConstants.DASHBOARD_HEIGHT_OVER_WIDTH + pageWidth * DrawingConstants.CHARACTERS_BEGINNING_PROPORTION;
        int heightProportion = 13;

        for (CharacterState character : characters) {
            String imagePath = "/gameboard/characters/" + character.getCharacterName() + ".jpg";
            ImageView characterImage = new ImageView(new Image(imagePath));
            characterImage.setPreserveRatio(true);
            characterImage.setFitWidth(pageWidth * DrawingConstants.CHARACTER_CARD_PROPORTION);

            BorderPane characterToAdd = new BorderPane(characterImage);
            characterToAdd.setLayoutX(coordX);
            characterToAdd.setLayoutY(pageHeight / heightProportion);
            characterToAdd.setOnMouseClicked(event -> ObjectClickListeners.setCharacterClicked(character.getCharacterName(), characterToAdd));
            characterImages.add(characterToAdd);
            root.getChildren().add(characterToAdd);

            if (character.isHasCoin()) {
                double imageWidth = pageWidth * DrawingConstants.COIN_PROPORTION;
                ImageView coin = new ImageView(new Image("/gameboard/Moneta_base.png"));
                coin.setPreserveRatio(true);
                coin.setX(coordX + pageWidth * (DrawingConstants.CHARACTER_CARD_PROPORTION) - imageWidth);
                coin.setY(pageHeight * 0.21);
                coin.setFitWidth(imageWidth);
                root.getChildren().add(coin);
            }
            if (character.getStudents() != null) {
                GridPane grid = new GridPane();
                grid.setLayoutX(coordX + pageWidth * DrawingConstants.SPACE_BETWEEN_STUDENTS_ON_CHARACTERS);
                grid.setLayoutY(pageHeight / heightProportion - 2 * pageWidth * 0.4 / 25);
                List<BorderPane> studentOnCharacter = new ArrayList<>();
                for (int i = 0; i < character.getStudents().size(); i++) {
                    String studentPath = "/gameboard/students/student_" +
                            character.getStudents().get(i).getColor().toString().toLowerCase() + ".png";
                    ImageView student = new ImageView(new Image(studentPath));
                    student.setPreserveRatio(true);
                    student.setFitWidth(pageWidth * 0.4 / 25);

                    BorderPane studentPane = new BorderPane(student);
                    int index = i;
                    studentPane.setOnMouseClicked(event ->
                            ObjectClickListeners.setStudentsOnCardClicked(
                                    character.getStudents().get(index).getColor(), studentPane))
                    ;
                    studentOnCharacter.add(studentPane);

                    grid.add(studentPane, i / 2, i % 2);
                }
                studentsOnCharacter.put(character.getCharacterName(), studentOnCharacter);

                root.getChildren().add(grid);
            }

            coordX += pageWidth * (DrawingConstants.CHARACTER_CARD_PROPORTION + DrawingConstants.SPACE_BETWEEN_CHARACTERS_PROPORTION);
        }
    }

    private static ImageView getImageView(String path, double x, double y, double fitWidth) {
        ImageView iv = new ImageView(new Image(path));
        iv.setPreserveRatio(true);
        iv.setFitWidth(fitWidth);
        iv.setX(x);
        iv.setY(y);
        return iv;
    }

    private static GridPane getGridPane(double x, double y, double hgap, double vgap) {
        GridPane grid = new GridPane();
        grid.setLayoutX(x);
        grid.setLayoutY(y);
        grid.setHgap(hgap);
        grid.setVgap(vgap);
        return grid;
    }

    public static void setCurrentActions(List<String> currentActions) {
        // TODO highlight the corresponding parts
        for (String action : currentActions) {
            highlightAction(action);
        }
    }

    private static void highlightAction(String action) {
        switch (action) {
            case "MOVE_STUDENT_TO_DINING", "MOVE_STUDENT_TO_ISLAND" -> {
                entranceStudents.forEach(DrawingComponents::setGoldenBorder);
                diningGaps.forEach(DrawingComponents::setGoldenBorder);
                islands.forEach(DrawingComponents::setGoldenBorder);
            }
            case "PLAY_ASSISTANT" -> assistantCards.forEach(DrawingComponents::setGoldenBorder);
            case "PLAY_CHARACTER" -> characterImages.forEach(DrawingComponents::setGoldenBorder);
            case "MOVE_MN" -> islands.forEach(DrawingComponents::setGoldenBorder);
            case "FILL_FROM_CLOUD" -> cloudImages.forEach(DrawingComponents::setGoldenBorder);
        }
    }

    public static void removeGoldenBordersFromAllCharacters() {
        characterImages.forEach(character -> character.getStyleClass().clear());
    }

    private static void setGoldenBorder(Node element) {
        element.getStyleClass().add("highlight_element");
    }

    private static void setBlueBorders(Node element) {
        element.getStyleClass().add("element_active_for_swap_character");
    }

    private static void setGreenBorders(Node element) {
        element.getStyleClass().add("element_active_for_moving_character");
    }

    public static void addBlueBordersToEntranceStudents() {
        entranceStudents.forEach(DrawingComponents::setBlueBorders);
    }

    public static void addBlueBordersToCharacterStudents(CharacterName name) {
        studentsOnCharacter.get(name).forEach(DrawingComponents::setBlueBorders);
    }

    public static void addBlueBordersToDiningStudents() {
        diningStudents.forEach(DrawingComponents::setBlueBorders);
    }

    public static void moveStudentAwayFromCard(CharacterName name, boolean toIsland) {
        if (studentsOnCharacter.containsKey(name)) {
            removeGoldenBordersFromAllElements();
            List<BorderPane> students = studentsOnCharacter.get(name);
            students.forEach(DrawingComponents::setGreenBorders);
            if (toIsland) {
                islands.forEach(DrawingComponents::setGreenBorders);
            } else {
                diningGaps.forEach(DrawingComponents::setGreenBorders);
            }
        }
    }

    public static void askIslandIndex() {
        removeGoldenBordersFromAllElements();
        islands.forEach(island -> island.getStyleClass().add("element_active_for_island_character"));
    }

    public static void removeGoldenBordersFromAllElements() {
        diningGaps.forEach(gap -> gap.getStyleClass().clear());
        entranceStudents.forEach(student -> student.getStyleClass().clear());
        assistantCards.forEach(assistant -> assistant.getStyleClass().clear());
        cloudImages.forEach(cloud -> cloud.getStyleClass().clear());
        assistantCards.forEach(card -> card.getStyleClass().clear());
        islands.forEach(island -> island.getStyleClass().clear());
        studentsOnCharacter.values().forEach(list -> list.forEach(student -> student.getStyleClass().clear()));
        characterImages.forEach(character -> character.getStyleClass().clear());
    }

    public static void clearAll(AnchorPane root) {
        root.getChildren().clear();

        characterImages.clear();
        cloudImages.clear();
        assistantCards.clear();
        entranceStudents.clear();
        diningStudents.clear();
        diningGaps.clear();
        studentsOnCharacter.clear();
        studentsOnIslands.clear();
        noEntriesOnIslands.clear();
        islands.clear();
    }

}
