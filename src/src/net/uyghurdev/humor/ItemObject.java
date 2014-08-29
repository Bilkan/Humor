package net.uyghurdev.humor;

public class ItemObject {

	private int ServerID;
	private String Title;
	private String Content;
	private boolean Favorate;
	private boolean Read;

	ItemObject(){}
	
	public void setServerID(int id){
		ServerID = id;
	}
	
	public int getServerID(){
		return ServerID;
	}
	
	public void setTitle(String title){
		Title = title;
	}
	
	public String getTitle(){
		return Title;
	}
	
	public void setContent(String content){
		Content = content;
	}
	
	public String getContent(){
		return Content;
	}
	
	public void setFavorate(boolean favorate){
		Favorate = favorate;
	}

	public boolean getFavorate() {
		// TODO Auto-generated method stub
		return Favorate;
	}
	
	public void setRead(boolean read){
		Read = read;
	}
	
	public boolean getRead(){
		return Read;
	}
	
}
