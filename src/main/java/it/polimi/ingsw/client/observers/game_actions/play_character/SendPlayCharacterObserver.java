package it.polimi.ingsw.client.observers.game_actions.play_character;

import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.utils.constants.Constants;
import it.polimi.ingsw.messages.action.Action;
import it.polimi.ingsw.messages.action.ActionArgs;
import it.polimi.ingsw.messages.action.ClientActionMessage;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;

import java.util.List;

public class SendPlayCharacterObserver implements PlayCharacterObserver {

    private final MessageHandler mh;

    public SendPlayCharacterObserver(MessageHandler mh) {
        this.mh = mh;
        mh.attachPlayCharacterObserver(this);
    }

    @Override
    public void onCharacterPlayed(CharacterName name, Color color, Integer island, List<Color> srcStudents, List<Color> dstStudents) {
        ActionArgs args = new ActionArgs();
        args.setCharacterName(name);
        args.setColor(color);
        args.setIsland(island);
        args.setSourceStudents(srcStudents);
        args.setDstStudents(dstStudents);

        Action action = new Action(Constants.ACTION_PLAY_CHARACTER, args);

        ClientActionMessage toSend = new ClientActionMessage();
        toSend.setAction(action);
        toSend.setPlayer(mh.getNetworkClient().getClient().getUsername());

        mh.getNetworkClient().sendMessageToServer(toSend.toJson());
    }
}
