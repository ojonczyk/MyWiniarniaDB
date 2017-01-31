import javax.swing.JButton;

public class JButtonMainMenu extends JButton{
	private static final long serialVersionUID = 1L;

	public JButtonMainMenu(){
		super();
		this.setSize(300, 100);
	}
	public JButtonMainMenu(String text){
		super(text);
		this.setSize(400, 200);
	}
};