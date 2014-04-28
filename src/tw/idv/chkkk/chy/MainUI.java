package tw.idv.chkkk.chy;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.InputStream;

/**
 * FX版 主進入點
 * @author Nicholase Chen
 */
public class MainUI extends Application
{

	@Override
	public void start(Stage primaryStage)
	{

		String fxml = "mainUI.fxml";	// UI fxml Source

		// ----- 以 FXMLLoader與 JavaFXBuilerFactory方式 開啟UI，以取得Controller
		FXMLLoader loader = new FXMLLoader();
		InputStream in = MainUI.class.getResourceAsStream(fxml);
		loader.setBuilderFactory(new JavaFXBuilderFactory());
		loader.setLocation(MainUI.class.getResource(fxml));
		AnchorPane page;
		try
		{
			try
			{
				page = (AnchorPane) loader.load(in);
			}
			finally
			{
				in.close();
			}

			// 取得 MainUI的 Controller
			final MainUI_Controller MController = (MainUI_Controller) loader.getController();

			// 設定stage
			Scene scene = new Scene(page);
			primaryStage.setTitle("猜數字 v2.0");
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();
			primaryStage.setResizable(false);
			primaryStage.getIcons().add(new Image("/images/icon.png")); // 設定視窗icon

			// 建立 config
			Config config = new Config();
			MController.config = config;	// 傳回給controller

			// 關閉主視窗時的動作，呼叫Main_controller中的 ExitGame()做確認
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
			{

				@Override
				public void handle(WindowEvent e)
				{
					MController.ExitGame();
					e.consume();	// 取消關閉，繼續執行
				}
			});

			primaryStage.show();	// 開啟主UI
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
