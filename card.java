import org.openqa.selenium.WebElement;

public class card {
	public WebElement elem;
	public int score;
	public int type;
	public String title;
	
	
	public card(WebElement elem,int score,String title) {
		this.elem=elem;
		this.score=score;
		this.title=title;
	}
	public int getScore() {
		return score;
	}

}
