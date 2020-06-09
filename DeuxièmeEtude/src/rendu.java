import javax.swing.JLabel;

public class rendu {
	
	public String type;
	public JLabel pic;
	
	public rendu(String type, JLabel pic) {
		super();
		this.type = type;
		this.pic = pic;
	}
	
	public rendu() {
		// TODO Auto-generated constructor stub
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public JLabel getPic() {
		return pic;
	}
	public void setPic(JLabel pic) {
		this.pic = pic;
	}
	

}
