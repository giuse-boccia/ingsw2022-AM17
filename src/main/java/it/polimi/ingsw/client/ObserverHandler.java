package it.polimi.ingsw.client;

import it.polimi.ingsw.client.observers.choices.action.ActionChoiceObserver;
import it.polimi.ingsw.client.observers.choices.character.CharacterChoiceObserver;
import it.polimi.ingsw.client.observers.game_actions.choose_cloud.ChooseCloudObserver;
import it.polimi.ingsw.client.observers.game_actions.move_mn.MoveMNObserver;
import it.polimi.ingsw.client.observers.game_actions.move_student.MoveStudentObserver;
import it.polimi.ingsw.client.observers.game_actions.play_assistant.PlayAssistantObserver;
import it.polimi.ingsw.client.observers.game_actions.play_character.PlayCharacterObserver;
import it.polimi.ingsw.client.observers.login.game_parameters.GameParametersObserver;
import it.polimi.ingsw.client.observers.login.load_game.LoadGameObserver;
import it.polimi.ingsw.client.observers.login.username.UsernameObserver;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;

import java.util.List;

public interface ObserverHandler {

    /**
     * Adds the given observer to the list of all UsernameObservers
     *
     * @param usernameObserver the {@code UsernameObserver} to be added
     */
    void attachUsernameObserver(UsernameObserver usernameObserver);

    /**
     * Adds the given observer to the list of all GameParametersObservers
     *
     * @param parametersObserver the {@code GameParametersObserver} to be added
     */
    void attachGameParametersObserver(GameParametersObserver parametersObserver);

    /**
     * Adds the given observer to the list of all MoveStudentObservers
     *
     * @param moveStudentObserver the {@code MoveStudentObserver} to be added
     */
    void attachMoveStudentObserver(MoveStudentObserver moveStudentObserver);

    /**
     * Adds the given observer to the list of all MoveMNObservers
     *
     * @param mnObserver the {@code MoveMNObserver} to be added
     */
    void attachMoveMNObserver(MoveMNObserver mnObserver);

    /**
     * Adds the given observer to the list of all ChooseCloudObservers
     *
     * @param cloudObserver the {@code ChooseCloudObserver} to be added
     */
    void attachChooseCloudObserver(ChooseCloudObserver cloudObserver);

    /**
     * Adds the given observer to the list of all PlayAssistantObservers
     *
     * @param playAssistantObserver the {@code PlayAssistantObserver} to be added
     */
    void attachPlayAssistantObserver(PlayAssistantObserver playAssistantObserver);

    /**
     * Adds the given observer to the list of all PlayCharacterObservers
     *
     * @param playCharacterObserver the {@code PlayCharacterObserver} to be added
     */
    void attachPlayCharacterObserver(PlayCharacterObserver playCharacterObserver);

    /**
     * Adds the given observer to the list of all ActionChoiceObservers
     *
     * @param actionChoiceObserver the {@code ActionChoiceObserver} to be added
     */
    void attachActionChoiceObserver(ActionChoiceObserver actionChoiceObserver);

    /**
     * Adds the given observer to the list of all CharacterChoiceObservers
     *
     * @param characterChoiceObserver the {@code CharacterChoiceObserver} to be added
     */
    void attachCharacterChoiceObserver(CharacterChoiceObserver characterChoiceObserver);

    /**
     * Adds the given observer to the list of all LoadGameObservers
     *
     * @param loadGameObserver the {@code LoadGameObserver} to be added
     */
    void attachLoadGameObserver(LoadGameObserver loadGameObserver);

    /**
     * Notifies all the attached observers that the user has selected their username
     *
     * @param message the selected username
     */
    void notifyAllUsernameObservers(String message);

    /**
     * Notifies all the attached observers that the user has selected the game parameters
     *
     * @param numPlayers the number of player chosen
     * @param isExpert   true if the game is in Expert mode
     */
    void notifyAllGameParametersObservers(int numPlayers, boolean isExpert);

    /**
     * Notifies all the attached observers that a student has been moved
     *
     * @param color       the color of the moved student
     * @param islandIndex the index of the island where the student has eventually been moved to
     */
    void notifyMoveStudentObservers(Color color, Integer islandIndex);

    /**
     * Notifies all the attached observers that Mother Nature has been moved
     *
     * @param numSteps the number of steps Mother Nature has been moved of
     */
    void notifyMoveMNObservers(int numSteps);

    /**
     * Notifies all the attached observers that the user has selected a cloud
     *
     * @param index the index of the selected cloud
     */
    void notifyChooseCloudObservers(int index);

    /**
     * Notifies all the attached observers that the user has played an assistant
     *
     * @param index the index of the selected assistant
     */
    void notifyPlayAssistantObservers(int index);

    /**
     * Notifies all the attached observers that the user has played a character
     *
     * @param name        the name of the played character
     * @param color       the color needed for the character effect to develop
     * @param island      the index of the island needed for the character effect to develop
     * @param srcStudents the students to move/swap
     * @param dstStudents the students to eventually swap with srcStudents
     */
    void notifyPlayCharacterObservers(CharacterName name, Color color, Integer island, List<Color> srcStudents, List<Color> dstStudents);

    /**
     * Notifies all the attached observers that the user has chosen an action to be done
     *
     * @param name the action selected
     */
    void notifyActionChoiceObservers(String name);

    /**
     * Notifies all the attached observers that the user has chosen a character
     *
     * @param name the name of the selected character
     */
    void notifyCharacterChoiceObservers(CharacterName name);

    /**
     * Notifies all the attached observers that the user has chosen to load a game
     */
    void notifyAllLoadGameObservers();

}
