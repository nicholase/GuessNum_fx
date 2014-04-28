package tw.idv.chkkk.chy;

public class Config {
	private int countQuestion = 4; //預設問題位數
	private int countGuess = 10; //預設猜題次數

	public Config() {
	}

	//設定CONFIG
	public Config(int countQuestion, int countGuess) {
		if (countQuestion > 0 && countGuess > 0) {
			this.countQuestion = countQuestion;
			this.countGuess = countGuess;
		}
	}

	/**
	 * 取得問題位數
	 * @return
	 */
	public int getCountQuestion() {
		return countQuestion;
	}

	/**
	 * 取得猜題次數
	 * @return
	 */
	public int getCountGuess() {
		return countGuess;
	}
	
	/**
	 * 設定問題位數
	 * @param countQuestion
	 */
	public void setCountQuestion(int countQuestion) {
		if (countQuestion > 0) this.countQuestion = countQuestion;
	}

	/**
	 * 設定猜題次數
	 * @param countGuess
	 */
	public void setCountGuess(int countGuess) {
		if (countGuess > 0) this.countGuess = countGuess;
	}
}
