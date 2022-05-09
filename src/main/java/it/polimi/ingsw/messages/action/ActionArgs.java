package it.polimi.ingsw.messages.action;

import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;

import java.util.List;

public class ActionArgs {

    private Color color;
    private Integer island;
    private Integer num_steps;
    private Integer cloud;
    private Integer value;
    private CharacterName characterName;
    private List<Color> sourceStudents;
    private List<Color> dstStudents;

    public ActionArgs() {
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Integer getIsland() {
        return island;
    }

    public void setIsland(Integer island) {
        this.island = island;
    }

    public Integer getNum_steps() {
        return num_steps;
    }

    public void setNum_steps(Integer num_steps) {
        this.num_steps = num_steps;
    }

    public Integer getCloud() {
        return cloud;
    }

    public void setCloud(Integer cloud) {
        this.cloud = cloud;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public CharacterName getCharacterName() {
        return characterName;
    }

    public void setCharacterName(CharacterName characterName) {
        this.characterName = characterName;
    }

    public List<Color> getSourceStudents() {
        return sourceStudents;
    }

    public void setSourceStudents(List<Color> sourceStudents) {
        this.sourceStudents = sourceStudents;
    }

    public List<Color> getDstStudents() {
        return dstStudents;
    }

    public void setDstStudents(List<Color> dstStudents) {
        this.dstStudents = dstStudents;
    }
}
