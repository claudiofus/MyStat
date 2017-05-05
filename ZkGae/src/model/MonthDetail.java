package model;

public class MonthDetail {
	String period;
	int netSalary;
	float holidays;
	float leaves;
	
	public MonthDetail(String period, float holidays, float leaves, int netSalary){
		this.period = period;
		this.netSalary = netSalary;
		this.holidays = holidays;
		this.leaves = leaves;
	}

	public String getPeriod() {
		return period;
	}
	
	public void setPeriod(String period) {
		this.period = period;
	}

	public int getNetSalary() {
		return netSalary;
	}

	public void setNetSalary(int netSalary) {
		this.netSalary = netSalary;
	}

	public float getHolidays() {
		return holidays;
	}

	public void setHolidays(float holidays) {
		this.holidays = holidays;
	}

	public float getLeaves() {
		return leaves;
	}

	public void setLeaves(float leaves) {
		this.leaves = leaves;
	}
}
