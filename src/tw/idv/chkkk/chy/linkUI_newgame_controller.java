package tw.idv.chkkk.chy;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class linkUI_newgame_controller implements Initializable
{

	public Config config;
	public Player player;
	@FXML
	private Button btnOK, btnCancel, btnDefault;
	@FXML
	private ChoiceBox<Integer> cbQCount, cbGCount;
	@FXML
	private ChoiceBox<String> cbMode;
	@FXML
	private Label labIP;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		initIPAddr();
	}

	// 初始化 choicebox 內容
	public void initChoiceBox()
	{
		// ----- 設定 出題位數 -----
		Integer[] qItem =
		{ 2, 3, 4, 5 };
		for (int i = 0; i < qItem.length; i++)
		{
			cbQCount.getItems().add(qItem[i]);
			if (qItem[i] == config.getCountQuestion())
			{
				cbQCount.getSelectionModel().select(i);	// 設定 預設值
			}
		}

		// ----- 設定 猜題次數 -----
		Integer[] gItem =
		{ 5, 10, 15, 20, 99 };
		for (int i = 0; i < gItem.length; i++)
		{
			cbGCount.getItems().add(gItem[i]);
			if (gItem[i] == config.getCountGuess()) // 設定 預設值
			{
				cbGCount.getSelectionModel().select(i);
			}
		}
		// ----- 設定 遊戲模式 -----
		cbMode.getItems().add("組隊模式");
		cbMode.getItems().add("PK模式");
		cbMode.getSelectionModel().select(1);
	}

	public void initIPAddr()
	{
		try
		{
			String[] ipaddr = ((InetAddress.getLocalHost()).toString()).split("/");
			labIP.setText("本機IP:" + ipaddr[1]);
		}
		catch (UnknownHostException e)
		{

		}

	}

	// configUI按鈕Action
	@FXML
	public void setAction(ActionEvent e)
	{
		// 取得stage
		Stage stage = (Stage) btnOK.getScene().getWindow();
		if (e.getSource().equals(btnOK)) // btnOK按下
		{
			// 取得選擇的值,存入config
			config.setCountQuestion((int) cbQCount.getSelectionModel().getSelectedItem());
			config.setCountGuess((int) cbGCount.getSelectionModel().getSelectedItem());

			player.isLinked = true;
			player.server = true;
			player.pk_mode = (cbMode.getSelectionModel().getSelectedIndex() == 1) ? 1 : 0;

			stage.close();	// 關閉視窗
		}
		if (e.getSource().equals(btnCancel))
		{
			stage.close();	// 直接關閉視窗
		}
		if (e.getSource().equals(btnDefault))
		{
			// 選回預設值 4位數與10次
			cbQCount.getSelectionModel().select(2);
			cbGCount.getSelectionModel().select(1);
		}

	}
}
