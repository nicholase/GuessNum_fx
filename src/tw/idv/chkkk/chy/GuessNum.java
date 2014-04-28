package tw.idv.chkkk.chy;

public class GuessNum {
	private int ans[] = { 0, 0 }; //xAxB陣列
	private String lastStr; //最後一次猜測的數字
	private SetQuestion sq; //問題
	private int guessed; //已猜次數
	private boolean pass; //是否通關

	public GuessNum(SetQuestion sq) {
		setQuestion(sq); //呼叫初始化void:setSQuestion
	}

	/**
	 * 初始化並設定新的Question
	 * @param sq
	 */
	public void setQuestion(SetQuestion sq) {
		this.sq = sq;
		this.lastStr = null;
		this.ans[0] = 0;
		this.ans[1] = 0;
		this.guessed = 0;
		this.pass = false;
	}

	/**
	 * 比對輸入的答案
	 * @param gStr
	 */
	public void guess(String gStr) {
		lastStr = gStr; //記錄最後一次猜的數字
		String qStr = sq.getQuestion(); //取回問題
		ans[0] = 0; //重設xAxB
		ans[1] = 0;

		for (int q = 0; q < qStr.length(); q++) {
			for (int g = 0; g < gStr.length(); g++) {
				if (gStr.charAt(g) == qStr.charAt(q)) {
					if (q == g) ans[0]++;
					if (q != g) ans[1]++;
				}
			}
		}
		guessed++; //記錄已猜次數
		pass = ((ans[0] == sq.getQuestionCount()) ? true : false); //是否過關
	}

	/**
	 * 手動設定是否過關 (初始化用)
	 * @param b
	 */
	public void setPass(boolean b) {
		pass = b;
	}
	
	/**
	 * 取得猜測結果
	 * @return  nAnB
	 */
	public String getResult() {
		return ans[0] + "A" + ans[1] + "B";
	}

	/**
	 * 取回是否過關
	 * @return
	 */
	public boolean getPass() {
		return pass;
	}

	/**
	 * 確認遊戲是否結束
	 * @return True or false (過關 或是 剩於次數為0)
	 */
	public boolean isFinsh() {
		return (pass || getRemainCount() == 0) ? true : false;
	}

	/**
	 * 取得已猜次數
	 * @return
	 */
	public int getGuessedCount() {
		return guessed;
	}

	/**
	 * 取得剩餘次數
	 * @return
	 * (猜題次數-已猜次數)
	 */
	public int getRemainCount() {
		return sq.getGuessCount() - guessed;
	}

	/**
	 * 取得最後一次猜測的數字
	 * @return
	 */
	public String getLastGuess() {
		return lastStr;
	}

}
