package tw.idv.chkkk.chy;

import java.util.LinkedHashSet;
import java.util.Set;

public class SetQuestion
{
	private String strQ_Nums; // 問題String
	private Set<Integer> qNums = new LinkedHashSet<Integer>(); // 問題set
	private Config config;

	public SetQuestion(Config config)
	{
		resetQuestion(config);
	}

	/**
	 * 初始化並產生新的問題
	 * @param config
	 */
	public boolean resetQuestion(Config config)
	{
		this.config = config; // 取得參數
		qNums.clear(); // 初始化
		strQ_Nums = null;
		do
		{
			this.qNums.add((int) (Math.random() * 9 + 1)); // 產生1-9 random
		} while (this.qNums.size() < this.config.getCountQuestion()); // 直到符合位數為止

		// 回存問題String
		for (Integer v : qNums)
		{
			strQ_Nums = (strQ_Nums == null) ? (strQ_Nums = v.toString()) : (strQ_Nums + v.toString());
		}
		return true;
	}

	/**
	 * 回傳本次問題
	 * @return
	 */
	public String getQuestion()
	{
		return strQ_Nums;
	}

	/**
	 * 回傳目前問題位數
	 * @return
	 */
	public int getQuestionCount()
	{
		return config.getCountQuestion();
	}

	/**
	 * 回傳目前問題可猜次數
	 */
	public int getGuessCount()
	{
		return config.getCountGuess();
	}

	/**
	 * 手動設定問題及位數
	 */
	public void setQuestion(String strQ_Nums, int qCount)
	{
		this.strQ_Nums = strQ_Nums;
		this.config.setCountQuestion(qCount);
	}

	/**
	 * 手動設定猜題次數
	 */
	public void setCountGuess(int gCount)
	{
		this.config.setCountGuess(gCount);
	}
}
