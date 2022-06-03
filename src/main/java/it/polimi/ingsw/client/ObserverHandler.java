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

    void attachUsernameObserver(UsernameObserver usernameObserver);

    void attachGameParametersObserver(GameParametersObserver parametersObserver);

    void attachMoveStudentObserver(MoveStudentObserver moveStudentObserver);

    void attachMoveMNObserver(MoveMNObserver mnObserver);

    void attachChooseCloudObserver(ChooseCloudObserver cloudObserver);

    void attachPlayAssistantObserver(PlayAssistantObserver playAssistantObserver);

    void attachPlayCharacterObserver(PlayCharacterObserver playCharacterObserver);

    void attachActionChoiceObserver(ActionChoiceObserver actionChoiceObserver);

    void attachCharacterChoiceObserver(CharacterChoiceObserver characterChoiceObserver);

    void attachLoadGameObserver(LoadGameObserver loadGameObserver);

    void notifyAllUsernameObservers(String message);

    void notifyAllGameParametersObservers(int numPlayers, boolean isExpert);

    void notifyMoveStudentObservers(Color color, Integer islandIndex);

    void notifyMoveMNObservers(int numSteps);

    void notifyChooseCloudObservers(int index);

    void notifyPlayAssistantObservers(int index);

    void notifyPlayCharacterObservers(CharacterName name, Color color, Integer island, List<Color> srcStudents, List<Color> dstStudents);

    void notifyActionChoiceObservers(String name);

    void notifyCharacterChoiceObservers(CharacterName name);

    void notifyAllLoadGameObservers();

}
