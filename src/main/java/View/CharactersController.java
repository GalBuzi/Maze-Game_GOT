package View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class CharactersController {
    @FXML
    public javafx.scene.control.Button btn_pickedChar;
    static public boolean HalisiTrue = false;
    static public boolean JonTrue = false;
    static public boolean CerseiTrue = false;
    static public boolean NiightKingTrue = false;
    static public boolean StannisTrue = false;
    static public boolean BranTrue = false;
    static public boolean LittleFingerTrue = false;
    static public boolean SansaTrue = false;


    public ToggleGroup   players; //I called it myGroup in SceneBuilder as well.


    public void pick(ActionEvent actionEvent) {
        MyViewController.stagecharacter=true;
        Stage stage = (Stage) btn_pickedChar.getScene().getWindow();
        stage.close();
    }


    public void HalisiTrue(ActionEvent actionEvent) {
        HalisiTrue=true;
        JonTrue=false;
        CerseiTrue=false;
        NiightKingTrue=false;
        StannisTrue=false;
        BranTrue=false;
        LittleFingerTrue=false;
        SansaTrue=false;
    }
    public void JonTrue(ActionEvent actionEvent) {
        HalisiTrue=false;
        JonTrue=true;
        CerseiTrue=false;
        NiightKingTrue=false;
        StannisTrue=false;
        BranTrue=false;
        LittleFingerTrue=false;
        SansaTrue=false;
    }
    public void CerseiTrue(ActionEvent actionEvent) {
        HalisiTrue=false;
        JonTrue=false;
        CerseiTrue=true;
        NiightKingTrue=false;
        StannisTrue=false;
        BranTrue=false;
        LittleFingerTrue=false;
        SansaTrue=false;
    }
    public void NightKingTrue(ActionEvent actionEvent) {
        HalisiTrue=false;
        JonTrue=false;
        CerseiTrue=false;
        NiightKingTrue=true;
        StannisTrue=false;
        BranTrue=false;
        LittleFingerTrue=false;
        SansaTrue=false;
    }

    public void StannisTrue(ActionEvent actionEvent) {
        HalisiTrue=false;
        JonTrue=false;
        CerseiTrue=false;
        NiightKingTrue=false;
        StannisTrue=true;
        BranTrue=false;
        LittleFingerTrue=false;
        SansaTrue=false;
    }

    public void BranTrue(ActionEvent actionEvent) {
        HalisiTrue=false;
        JonTrue=false;
        CerseiTrue=false;
        NiightKingTrue=false;
        StannisTrue=false;
        BranTrue=true;
        LittleFingerTrue=false;
        SansaTrue=false;
    }

    public void LittleFingerTrue(ActionEvent actionEvent) {
        HalisiTrue=false;
        JonTrue=false;
        CerseiTrue=false;
        NiightKingTrue=false;
        StannisTrue=false;
        BranTrue=false;
        LittleFingerTrue=true;
        SansaTrue=false;
    }

    public void SansaTrue(ActionEvent actionEvent) {
        HalisiTrue=false;
        JonTrue=false;
        CerseiTrue=false;
        NiightKingTrue=false;
        StannisTrue=false;
        BranTrue=false;
        LittleFingerTrue=false;
        SansaTrue=true;
    }

}
