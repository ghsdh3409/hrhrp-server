package kr.ac.kaist.hrhrp.type;

public class Selection {
	private String selection;
	private Face selectionFace;
	private String selectionType;
	
	public String getSelection() {
		return selection;
	}
	
	public Face getSelectionFace() {
		return selectionFace;
	}
	
	public String getSelectionType() {
		return selectionType;
	}
	
	public void setSelection(String selection) {
		this.selection = selection;
	}
	
	public void setSelectionFace(Face selectionFace) {
		this.selectionFace = selectionFace;
	}
	
	public void setSelectionType(String selectionType) {
		this.selectionType = selectionType;
	}
}
