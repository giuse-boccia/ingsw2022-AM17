package it.polimi.ingsw.client.observers.game_actions.move_student;

import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.utils.constants.Constants;
import it.polimi.ingsw.messages.action.Action;
import it.polimi.ingsw.messages.action.ActionArgs;
import it.polimi.ingsw.messages.action.ClientActionMessage;
import it.polimi.ingsw.model.game_objects.Color;

public class SendMoveStudentObserver implements MoveStudentObserver {

    private final MessageHandler mh;

    public SendMoveStudentObserver(MessageHandler mh) {
        this.mh = mh;
        mh.attachMoveStudentObserver(this);
    }


    @Override
    public void onStudentMoved(Color color, Integer islandIndex) {
        ActionArgs args = new ActionArgs();
        args.setColor(color);
        args.setIsland(islandIndex);

        String actionName = islandIndex != null ? Constants.ACTION_MOVE_STUDENT_TO_ISLAND : Constants.ACTION_MOVE_STUDENT_TO_DINING;
        Action action = new Action(actionName, args);

        ClientActionMessage toSend = new ClientActionMessage();
        toSend.setAction(action);
        toSend.setPlayer(mh.getNetworkClient().getClient().getUsername());

        mh.getNetworkClient().sendMessageToServer(toSend.toJson());
    }
}
