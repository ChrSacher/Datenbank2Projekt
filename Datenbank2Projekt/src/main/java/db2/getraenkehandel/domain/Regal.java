package db2.getraenkehandel.domain;

import javax.persistence.Entity;

import db2.BaseEntity;

@Entity
public class Regal extends BaseEntity<Long>
{
	private String standort;

	public Regal(String standort)
	{
		super();
		this.setStandort(standort);
	}

	public String getStandort()
	{
		return standort;
	}

	public void setStandort(String standort)
	{
		this.standort = standort;
	}

}
