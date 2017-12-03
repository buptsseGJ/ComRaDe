package cn.edu.thu.platform.entity;

public class Race {
	private String line1;
	private String line2;
	private String variable;
	private String packageClass;
	private String detail;
	private String time;
	private String variableType;
	private String codeStructure;
	private String methodSpan;
	private String sensitiveBranch;
	private String sensitiveLoop;
	private String cause;
	private String commonUsage;
	private String bug;

	public Race(String line1, String line2, String variable, String packageClass, String detail, String time,
			String variableType, String codeStructure, String methodSpan, String sensitiveBranch, String sensitiveLoop,
			String cause, String commonUsage,String bug) {
		super();
		this.line1 = line1;
		this.line2 = line2;
		this.variable = variable;
		this.packageClass = packageClass;
		this.detail = detail;
		this.time = time;
		this.variableType = variableType;
		this.codeStructure = codeStructure;
		this.methodSpan = methodSpan;
		this.sensitiveBranch = sensitiveBranch;
		this.sensitiveLoop = sensitiveLoop;
		this.cause = cause;
		this.commonUsage = commonUsage;
		this.bug = bug;
	}

	public void Update(){
		if(Integer.parseInt(this.line1)>Integer.parseInt(this.line2)) {
			String temp = this.line1;
			this.line1 = this.line2;
			this.line2 = temp;
		}
	}
	public Race(String line1, String line2) {
		this.line1 = line1;
		this.line2 = line2;
	}
	
	public Race(String line1,String line2,String packageClass){
		this.line1 = line1;
		this.line2 = line2;
		this.packageClass = packageClass;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getLine1() {
		return line1;
	}

	public String getLine2() {
		return line2;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public String getPackageClass() {
		return packageClass;
	}

	public void setPackageClass(String packageClass) {
		this.packageClass = packageClass;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getVariableType() {
		return variableType;
	}

	public void setVariableType(String variableType) {
		this.variableType = variableType;
	}

	public String getCodeStructure() {
		return codeStructure;
	}

	public void setCodeStructure(String codeStructure) {
		this.codeStructure = codeStructure;
	}

	public String getMethodSpan() {
		return methodSpan;
	}

	public void setMethodSpan(String methodSpan) {
		this.methodSpan = methodSpan;
	}

	public String getSensitiveBranch() {
		return sensitiveBranch;
	}

	public void setSensitiveBranch(String sensitiveBranch) {
		this.sensitiveBranch = sensitiveBranch;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getCommonUsage() {
		return commonUsage;
	}

	public void setCommonUsage(String commonUsage) {
		if(commonUsage==null){
			this.commonUsage = "";
		}else{
			this.commonUsage = commonUsage;
		}
	}

	public String getBug() {
		return bug;
	}

	public void setBug(String bug) {
		if(bug==null){
			this.bug = "";
		}else{
			this.bug = bug;
		}
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSensitiveLoop() {
		return sensitiveLoop;
	}

	public void setSensitiveLoop(String sensitiveLoop) {
		this.sensitiveLoop = sensitiveLoop;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((line1 == null) ? 0 : line1.hashCode());
		result = prime * result + ((line2 == null) ? 0 : line2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Race other = (Race) obj;
		if (line1 == null) {
			if (other.line1 != null)
				return false;
		} else if (!line1.equals(other.line1))
			return false;
		if (line2 == null) {
			if (other.line2 != null)
				return false;
		} else if (!line2.equals(other.line2))
			return false;
		return true;
	}
}
