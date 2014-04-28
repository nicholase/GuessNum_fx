package tw.idv.chkkk.chy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class linkUI_Controller implements Initializable
{
	public Config config;
	public Player player;
	@FXML
	Button btnJoin, btnServer;

	@FXML
	TextField txtName;

	@FXML
	void Action_LinkUI(ActionEvent e) throws IOException
	{
		player.player_name = txtName.getText();

		if (e.getSource().equals(btnJoin))
		{
			String ipaddr = JOptionPane.showInputDialog(null, "請輸入開局之電腦IP", "", JOptionPane.QUESTION_MESSAGE);
			player.ip_server = ipaddr;
			player.isLinked = true;
			player.server = false;
		}
		if (e.getSource().equals(btnServer))
		{

			// ----- 以 FXMLLoader與 JavaFXBuilerFactory方式 開啟UI，以利取得Controller
			String fxml = "linkUI_newgame.fxml";
			FXMLLoader loader = new FXMLLoader();
			InputStream in = MainUI_Controller.class.getResourceAsStream(fxml);
			loader.setBuilderFactory(new JavaFXBuilderFactory());
			loader.setLocation(MainUI_Controller.class.getResource(fxml));
			AnchorPane link;
			try
			{
				link = (AnchorPane) loader.load(in);
			}
			finally
			{
				in.close();
			}
			// 取得 LinkUI的 Controller
			linkUI_newgame_controller LUIController = (linkUI_newgame_controller) loader.getController();
			LUIController.config = config;
			LUIController.player = player;
			LUIController.initChoiceBox();

			Stage stage = new Stage();
			Scene sc = new Scene(link);
			stage.setScene(sc);
			stage.setTitle("開啟新局");
			stage.centerOnScreen();		// 設定位置
			stage.getIcons().add(new Image("/images/icon.png")); // 設定視窗icon

			stage.showAndWait();
		}
		Stage self = (Stage) btnJoin.getScene().getWindow();
		self.close();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{

	}

}
