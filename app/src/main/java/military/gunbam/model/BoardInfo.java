package military.gunbam.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BoardInfo implements Serializable {
    private String boardTitle;

    public BoardInfo(String boardTitle){
        this.boardTitle = boardTitle;
    }

    public String getboardTitle(){ return this.boardTitle = boardTitle; }
    public void setboardTitle(String boardTitle){ this.boardTitle = boardTitle; }
}