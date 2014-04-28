package tw.idv.chkkk.chy;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import tw.idv.chkkk.chy.AlertDialog.AlertDialog;
import tw.idv.chkkk.chy.AlertDialog.AlertDialog.BUTTON;
import tw.idv.chkkk.chy.AlertDialog.AlertDialog.ICON;
import tw.idv.chkkk.chy.AlertDialog.AlertDialog.OPTION;

class Player
{
	boolean server, isLinked, myturn, passed;
	String player_name;
	String ip_server;
	int port;
	String qNums, gNums, result;
	int no, pk_mode, qCount, gCount;

	Player()
	{
		isLinked = false;
		pk_mode = 1;
		server = false;
		player_name = "Player1";
		ip_server = null;
		port = 41415;
		qNums = null;
		gNums = null;
		result = null;
		no = 0;
		qCount = 0;
		gCount = 0;
		passed = false;

	}

}

public class MainUI_Controller implements Initializable
{
	// ----- 設定主要的class -----
	public Config config;
	public SetQuestion sq;
	public GuessNum gn;
	public Player player;

	// ----- 載入FXML裡的元件 -----
	@FXML
	private MenuItem mitemOpen, mitemLink, mitemConfig, mitemQuit, mitemAbout;
	@FXML
	private Button btnOpen, btnLink, btnConfig, btnAbout, btnQuit, btnOK;
	@FXML
	private Pane paneContent;
	@FXML
	private Label labTitle, labState, labTimer;
	@FXML
	private TextField txtInput;
	@FXML
	private TableView<Result> tableResult;
	@FXML
	private TableColumn clumPlayer, clumNO, clumGuess, clumResult;

	// 計時器 宣告
	private gameTimer gt = new gameTimer();

	// 結果表格的data
	ObservableList<Result> data;

	private Linker linker;
	private Thread link_update;
	public RS rs = new RS();

	/**
	 * 呼叫[建立新遊戲](Open)
	 */
	@FXML
	private void callOpen(ActionEvent e)
	{
		if (alreadyCheck())
		{
			player.isLinked = false;
			newGame();
		}
	}

	/**
	 * 呼叫[遊戲設定](configUI)
	 * @throws IOException
	 */
	@FXML
	private void callConfig(ActionEvent e) throws IOException
	{
		if (alreadyCheck())
		{
			TimerStop();
			showConfigUI();
			setPaneContent(false);

		}
	}

	/**
	 * 呼叫[連線模式](LinkUI)
	 */
	@FXML
	private void callLink(ActionEvent e) throws IOException
	{
		if (alreadyCheck())
		{
			player.isLinked = false;
			TimerStop();
			setPaneContent(false);
			cleanResult();
			showLinkUI();
		}
	}

	/**
	 * 呼叫[關於](About)
	 */
	@FXML
	private void callAbout(ActionEvent e)
	{
		AlertDialog.ShowAlertDialog("關於本程式", "猜數字 GuessNumber v2.0\nNicholase(nicholase@gamil.com)", 400, 150);
	}

	/**
	 * 呼叫[離開](Quit)
	 */
	@FXML
	public void callClose(ActionEvent e)
	{
		ExitGame();
	}

	/**
	 * 送出按鈕Action
	 */
	@FXML
	public void callSend(ActionEvent e)
	{
		inputCheck();

	}

	/**
	 * 初始化快捷鍵
	 */
	private void initKey()
	{
		mitemOpen.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		mitemQuit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
	}

	/**
	 * 初始化Game
	 */
	private void initGame()
	{
		config = new Config();	// 重設config
		sq = new SetQuestion(config);
		gn = new GuessNum(sq);
		player = new Player();

		txtInput.setText(null);	// 清除輸入框
		txtInput.setEditable(false);	// 設定輸入框無法存取
		setPaneContent(false);	// 隱藏內容的pane
	}

	/**
	 * 判斷是否已經有遊戲進行中
	 */
	public boolean alreadyCheck()
	{
		if (gn.isFinsh()) return true; // 遊戲已經完成,不須提示
		else
		{ // 已有遊戲進行中,提示是否放棄當局
			OPTION check = AlertDialog.ShowAlertDialog(ICON.WARNING, "放棄進度", "遊戲正在進行中，要放棄當前這局?", BUTTON.YES_NO);

			if (check == OPTION.OK_OPTION)
			{
				TimerStop();
				return true;
			}
			else return false;
		}
	}

	/**
	 * 確認是否有遊戲進行中，並離開
	 */
	public void ExitGame()
	{
		if (alreadyCheck())
		{
			OPTION check = AlertDialog.ShowAlertDialog(ICON.CLOSE, "關閉遊戲", "確定要離開遊戲?", BUTTON.YES_NO);
			if (check == OPTION.OK_OPTION)
			{	// 關閉程式
				Platform.exit();
				System.exit(0);
			}

		}
	}

	/**
	 * 設定猜題pane(paneContent)是否可視
	 */
	private void setPaneContent(Boolean b)
	{
		if (!b)
		{
			gn.setPass(true);
		}
		// 設定遊戲過關,停止alreadycheck的檢查
		btnOK.setDisable(!b);	// 停用btnOK
		paneContent.setVisible(b);	// 隱藏content pane
	}

	/**
	 * 更新StateBar內容
	 */
	private void setStateBar(String str)
	{
		labState.setText(str);
	}

	/**
	 * 輸入檢查 (1-9) 且不重複的數字
	 */
	private void inputCheck()
	{
		String strErr = "輸入有誤!請輸入" + sq.getQuestionCount() + "位數1-9的不重覆數字!";
		String iptText = txtInput.getText();
		Set<Integer> checkSet = new LinkedHashSet<>();
		if (iptText.length() == sq.getQuestionCount())
		{ // 驗證輸入位數==問題位數
			for (int i = 0; i < iptText.length(); i++)
			{
				int asciiChar = (int) iptText.charAt(i);
				// 比對是否為1-9的數字，是則加入 set
				if (asciiChar >= 49 && asciiChar <= 57) checkSet
						.add(Integer.parseInt(String.valueOf(iptText.charAt(i))));
			}
			// 驗證set個數是否等於問題位數 (是=沒重覆)
			if (checkSet.size() == sq.getQuestionCount())
			{
				// 輸入正確
				txtInput.setText(null); // 清空輸入欄
				runGuess(iptText); // 呼叫runGuess
			}
			else setStateBar(strErr); // 輸入猜的數字重複
		}
		else setStateBar(strErr); // 輸入位數不對 或 非1-9
	}

	/**
	 * 執行猜題動作
	 */
	private void runGuess(String iptText)
	{
		if (!iptText.equals("9999")) gn.guess(iptText); // 呼叫gn並猜
		if (gn.isFinsh())
		{ // 判斷是否結束 (贏了或是次數用完)
			if (gn.getPass())
			{
				setStateBar("YOU WIN! :))"); // 贏了

				if (player.isLinked)
				{
					if (rs.winner == null || rs.winner.equals("null"))
					{
						AlertDialog.ShowAlertDialog(ICON.INFO, "Congratulations!", "恭喜您獲勝了!", BUTTON.OK_ONLY);
						rs.winner = player.player_name;
					}
					else
					{
						if (player.pk_mode == 1)
						{
							setStateBar("YOU LOSE! :(   Answer is " + sq.getQuestion());
							AlertDialog.ShowAlertDialog(ICON.INFO, "Game Over!", "您的對手 " + rs.winner + "獲勝",
									BUTTON.OK_ONLY);
						}
						else
						{
							AlertDialog.ShowAlertDialog(ICON.INFO, "Congratulations!", "遊戲結束!", BUTTON.OK_ONLY);
						}
						// linker.linkClose();
						// player.isLinked = false;
					}
					player.passed = true;
				}
			}
			else
			{
				setStateBar("YOU LOSE! :(   Answer is " + sq.getQuestion()); // 輸了

				if (player.isLinked)
				{
					if (rs.winner.equals("null") || rs.winner == null)
					{
						rs.winner = "_draw_game_XXXXX_";
					}
					else if (rs.winner.equals("_draw_game_XXXXX_"))
					{
						AlertDialog.ShowAlertDialog(ICON.INFO, "Game Over!", "遊戲結束! 和局!", BUTTON.OK_ONLY);
					}
				}
			}
			txtInput.setEditable(false); // 鎖定猜題欄位
			btnOK.setDisable(true); // 鎖定ok按鈕
			btnOpen.requestFocus(); // focus回open按鈕
			TimerStop();	// 遊戲結束，停止計時
		}
		else
		{ // 遊戲尚未結束
			setStateBar("還剩下" + gn.getRemainCount() + "次機會!"); // 更新state
			txtInput.requestFocus(); // focus猜題欄
			if (player.isLinked) setStateBar("回合結束!等待對方猜題");
		}
		rs.player = null;
		rs.lastcount = gn.getLastGuess();
		rs.result = gn.getResult();
		rs.guessedCount = gn.getGuessedCount();

		if (player.isLinked)
		{
			rs.player = player.player_name;
			int turn_flag = 1;
			String server_flag = (player.server) ? "Server" : "Clinet";
			String msg = server_flag + ":" + player.passed + ":" + turn_flag + ":" + rs.player + ":"
					+ rs.guessedCount + ":" + rs.lastcount + ":" + rs.result
					+ ":" + rs.winner;
			linker.sendMsgOut(msg);
			btnOK.setDisable(true);
			player.myturn = false;

			if (gn.isFinsh())
			{
				linker.linkClose();
				player.isLinked = false;
			}
		}
		if (!iptText.equals("9999")) renewResult(rs.player, rs.guessedCount, rs.lastcount, rs.result); // 更新結果表格(猜的次數,猜的數字,結果xAxB)
	}

	/**
	 * 更新結果表格內容
	 * @param no
	 *            猜題次數
	 * @param guess
	 *            猜的數字
	 * @param result
	 *            結果
	 */
	private void renewResult(String player, Integer no, String guess, String result)
	{
		Result rs = new Result();
		rs.player.setValue(player);
		rs.no.setValue(no);
		rs.guess.setValue(guess);
		rs.result.setValue(result);
		data.add(rs);
	}

	/**
	 * 清除結果表格內容
	 */
	private void cleanResult()
	{
		data.clear();
	}

	/**
	 * 初始化結果表格
	 */
	private void initResult()
	{
		clumPlayer.setCellValueFactory(new PropertyValueFactory<Result, String>("player"));
		clumNO.setCellValueFactory(new PropertyValueFactory<Result, Integer>("no"));
		clumGuess.setCellValueFactory(new PropertyValueFactory<Result, String>("guess"));
		clumResult.setCellValueFactory(new PropertyValueFactory<Result, String>("result"));
		data = FXCollections.observableArrayList();
		tableResult.setItems(data);

	}

	/**
	 * 定義表格內容的類別
	 */
	public class Result
	{
		private SimpleStringProperty player = new SimpleStringProperty();
		private SimpleIntegerProperty no = new SimpleIntegerProperty();
		private SimpleStringProperty guess = new SimpleStringProperty();
		private SimpleStringProperty result = new SimpleStringProperty();

		public String getPlayer()
		{
			return player.get();
		}

		public Integer getNo()
		{
			return no.get();
		}

		public String getGuess()
		{
			return guess.get();
		}

		public String getResult()
		{
			return result.get();
		}
	}

	/**
	 * 計時器重啟
	 */
	private void TimerReStart()
	{
		gt = new gameTimer();
		gt.stop();
		gt.reset();
		gt.start();
	}

	/**
	 * 計時器停止
	 */
	private void TimerStop()
	{
		gt.stop();
	}

	/**
	 * 計時器主類別
	 */
	public class gameTimer
	{
		private int totalTimeMisec = 0;
		private int mins = 0, sec = 0, misec = 0;
		private String tTime;
		public Timer timer = new Timer();

		public void start()
		{
			timer = new Timer();
			timer.schedule(new taskT(), 0, 10);
		}

		public void stop()
		{
			timer.cancel();
			timer = new Timer();
		}

		public void reset()
		{
			mins = 0;
			sec = 0;
			misec = 0;
			tTime = null;
		}

		public class taskT extends TimerTask
		{

			@Override
			public void run()
			{
				totalTimeMisec++;
				misec = totalTimeMisec % 100;
				sec = totalTimeMisec / 100;
				mins = totalTimeMisec / 100 / 60;

				tTime = String.format("%02d:%02d:%02d%n", mins, sec, misec);

				// *********** JavaFx元件在TimerTask中要renew，需丟進 platform.runlater中，否則會有thread錯誤
				Platform.runLater(new Runnable()
				{

					@Override
					public void run()
					{
						labTimer.setText(tTime);	// 更新timer label

					}
				});
			}

		}

	}

	/**
	 * 顯示configUI
	 */
	private void showConfigUI() throws IOException
	{

		// ----- 以 FXMLLoader與 JavaFXBuilerFactory方式 開啟UI，以利取得Controller
		String fxml = "configUI.fxml";
		FXMLLoader loader = new FXMLLoader();
		InputStream in = MainUI_Controller.class.getResourceAsStream(fxml);
		loader.setBuilderFactory(new JavaFXBuilderFactory());
		loader.setLocation(MainUI_Controller.class.getResource(fxml));
		AnchorPane page;
		try
		{
			page = (AnchorPane) loader.load(in);
		}
		finally
		{
			in.close();
		}
		// 取得 configUI的 Controller
		ConfigUI_Controller CUIController = (ConfigUI_Controller) loader.getController();

		Stage stage = new Stage();
		Scene scene = new Scene(page);
		stage.setScene(scene);
		stage.sizeToScene();
		stage.setTitle("遊戲設定");	// 設定視窗標題
		stage.centerOnScreen();		// 設定位置
		stage.setResizable(false);
		stage.getIcons().add(new Image("/images/icon.png")); // 設定視窗icon

		CUIController.config = config;
		CUIController.initChoiceBox(); // 初始化choicebox

		stage.showAndWait();  // 顯示configUI
	}

	/**
	 * 顯示linkUI_mod
	 */
	private void showLinkUI() throws IOException
	{
		// ----- 以 FXMLLoader與 JavaFXBuilerFactory方式 開啟UI，以利取得Controller
		String fxml = "linkUI_mod.fxml";
		FXMLLoader loader = new FXMLLoader();
		InputStream in = MainUI_Controller.class.getResourceAsStream(fxml);
		loader.setBuilderFactory(new JavaFXBuilderFactory());
		loader.setLocation(MainUI_Controller.class.getResource(fxml));
		GridPane page;
		try
		{
			page = (GridPane) loader.load(in);
		}
		finally
		{
			in.close();
		}
		// 取得 LinkUI的 Controller
		linkUI_Controller LUIController = (linkUI_Controller) loader.getController();

		Stage stage = new Stage();
		Scene scene = new Scene(page);
		stage.setScene(scene);
		stage.sizeToScene();
		stage.setTitle("連線模式");	// 設定視窗標題
		stage.centerOnScreen();		// 設定位置
		stage.setResizable(false);
		stage.getIcons().add(new Image("/images/icon.png")); // 設定視窗icon

		LUIController.config = config;
		LUIController.player = player;

		stage.showAndWait();  // 顯示LinkUI

		try
		{
			if (player.isLinked) link_start();
		}
		catch (Exception e)
		{}
	}

	/**
	 * UI初始化
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		initKey();	// 設定快捷鍵
		initGame();	// 初始化遊戲
		initResult();	// 初始化結果表格
	}

	/**
	 * 建立新遊戲
	 */
	private void newGame()
	{

		sq.resetQuestion(config); // 重設sq

		if (!player.server && player.isLinked)
		{
			sq.setQuestion(player.qNums, player.qCount);
			sq.setCountGuess(player.gCount);
			clumPlayer.setVisible(true);
		}
		gn.setQuestion(sq); // 重設gn

		labTitle.setText("請輸入" + sq.getQuestionCount() + "位不重複數字"); // 更新label
		setStateBar("遊戲開始"); // 更新state
		cleanResult(); // 清除結果表格
		txtInput.setEditable(true); // 設定猜題欄可寫入
		txtInput.requestFocus(); // focus猜題欄
		setPaneContent(true); // 設定猜題panel可視

		if (!player.isLinked) clumPlayer.setVisible(false); // 單人遊戲時，將表格中player隱藏

		TimerReStart(); // 開始計時

		// ---------- debug 用 ------------------
		txtInput.setText(sq.getQuestion());
		// ------------------------------------
	}

	private void link_start()
	{
		Stage root = (Stage) btnOK.getScene().getWindow();
		String ip = null;
		try
		{
			String[] ipaddr = ((InetAddress.getLocalHost()).toString()).split("/");
			ip = ipaddr[1];
		}
		catch (Exception e)
		{}

		root.setTitle("猜數字 v2.0 - " + ip + "【等待連線中】");
		linker = new Linker(player.ip_server, player.port, player.server);
		try
		{
			linker.linkStart();  // 建立連線
			player.isLinked = true;
		}
		catch (IOException e)
		{
			AlertDialog.ShowAlertDialog(ICON.CLOSE, "錯誤", "連線失敗!", BUTTON.OK_ONLY);
			root.setTitle("猜數字 v2.0");
			player.isLinked = false;
		}

		if (player.isLinked)
		{
			if (player.server)
			{
				root.setTitle("猜數字 v2.0【連線模式】" + ip);
				newGame();
				setStateBar("遊戲開始...現在是您的回合!");
				String msg = player.pk_mode + ":"
						+ sq.getQuestion() + ":"
						+ sq.getQuestionCount() + ":"
						+ sq.getGuessCount();
				linker.sendMsgOut(msg);
			}
			else
			{
				root.setTitle("猜數字 v2.0【連線模式】" + ip);
				String tmp = linker.getMsgin();
				String gmsg[] = (tmp).split(":");
				player.pk_mode = Integer.parseInt(gmsg[0]);
				player.qNums = gmsg[1];
				player.qCount = Integer.parseInt(gmsg[2]);
				player.gCount = Integer.parseInt(gmsg[3]);
				newGame();
				setStateBar("遊戲開始...現在是對方的回合!");
				btnOK.setDisable(true);
			}
			link_update = new Thread(new link_upate_runnable());
			link_update.start();
		}

	}

	class link_upate_runnable implements Runnable
	{

		@Override
		public void run()
		{
			String tmp;
			while ((tmp = linker.getMsgin()) != null)
			{
				// System.out.println(tmp);
				String[] gmsg = tmp.split(":");

				player.passed = Boolean.valueOf(gmsg[1]);
				player.myturn = (gmsg[2].equals("1")) ? true : false;
				rs = new RS(gmsg[3], Integer.parseInt(gmsg[4]), gmsg[5], gmsg[6]);
				rs.serverflag = gmsg[0];
				rs.winner = gmsg[7];

				if (player.pk_mode == 1) rs.lastcount = "-";
				if (player.passed)
				{
					gn.setPass(true);
					linker.linkClose();
					link_update.interrupt();
				}

				Platform.runLater(new Runnable()
				{

					@Override
					public void run()
					{
						renewResult(rs.player, rs.guessedCount, rs.lastcount, rs.result); // 更新結果表格(猜的次數,猜的數字,結果xAxB)
						if ((player.passed && !rs.winner.isEmpty()))
						{
							runGuess("9999");
						}
						else
						{
							if (player.myturn)
							{
								btnOK.setDisable(false);
								setStateBar("現在是您的回合!" + "還剩" + gn.getRemainCount() + "次!");
							}
						}
					}

				});

			}
		}
	}
}

class RS
{
	public String player, lastcount, result, winner, serverflag;
	public int guessedCount;

	RS()
	{

	}

	RS(String player, int guessedCount, String lastcount, String result)
	{
		this.player = player;
		this.guessedCount = guessedCount;
		this.lastcount = lastcount;
		this.result = result;
	}
}

class Linker
{
	static int port;
	int i;
	String messagein, messageout;
	public BufferedReader br;
	public PrintStream ps;
	public boolean isServer;
	static String ipaddr;
	public Socket socket;

	public Linker(String ipaddr, int port, boolean isServer)
	{
		this.ipaddr = ipaddr;
		this.port = port;
		this.isServer = isServer;
	}

	public void linkStart() throws IOException
	{

		if (isServer)
		{
			ServerSocket SS = new ServerSocket(port);
			socket = SS.accept();
		}
		else
		{
			socket = new Socket(InetAddress.getByName(ipaddr), port);
		}

		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		ps = new PrintStream(socket.getOutputStream());

	}

	public String getMsgin()
	{
		try
		{
			return br.readLine();
		}
		catch (IOException e)
		{
			return null;
		}

	}

	public void sendMsgOut(String msg)
	{
		ps.println(msg);
	}

	public void linkClose()
	{
		try
		{
			br.close();
			ps.close();
			socket.close();
		}
		catch (IOException e)
		{}
	}
}
