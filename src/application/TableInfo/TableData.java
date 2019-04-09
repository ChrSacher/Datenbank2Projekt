package application.TableInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Handler;

import application.model.BaseEntity;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TableData
{

	private Map<String, TableColumnData> data = new LinkedHashMap<>();
	private String tableName = "";

	// TableColumn,ForeignTable
	private Map<String, String> foreignKeys = new HashMap<>();

	private ObservableList<BaseEntity> values = FXCollections.observableArrayList();

	// Copy of values entity
	private ObjectProperty<BaseEntity> selectedEntity = new SimpleObjectProperty();

	public static void loadTypeConverter()
	{

	}

	public TableData(ResultSet sqlQuery, ResultSet foreignKeys)
	{
		loadFromQuery(sqlQuery, foreignKeys);
	}

	public void loadFromQuery(ResultSet sqlQuery, ResultSet foreignKeysResult)
	{
		try
		{
			data.clear();
			ResultSetMetaData meta = sqlQuery.getMetaData();
			tableName = meta.getTableName(1);
			for (int i = 1; i <= meta.getColumnCount(); i++)
			{
				TableColumnData newData = new TableColumnData();
				newData.setName(meta.getColumnName(i));
				newData.setType(meta.getColumnTypeName(i));
				newData.setDisplayName(meta.getColumnLabel(i));
				data.put(meta.getColumnName(i), newData);
			}
			while (foreignKeysResult.next())
			{
				foreignKeys.put(foreignKeysResult.getString("FKCOLUMN_NAME"),
						foreignKeysResult.getString("PKTABLE_NAME"));
				data.get(foreignKeysResult.getString("FKCOLUMN_NAME")).setForeignColumn(foreignKeysResult.getString("PKCOLUMN_NAME"));
				data.get(foreignKeysResult.getString("FKCOLUMN_NAME")).setForeignTable(foreignKeysResult.getString("PKTABLE_NAME"));
			}
			loadValues(sqlQuery);

		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void loadValues(ResultSet sqlQuery)
	{
		values.clear();
		try
		{
			while (sqlQuery.next())
			{
				BaseEntity object = new BaseEntity();
				data.forEach((key, value) ->
				{
					try
					{
						String val = sqlQuery.getString(key);
						if (val != null)
						{
							object.setValue(key, val.trim());
						} else
						{
							object.setValue(key, null);
						}

					} catch (Exception e)
					{
						System.out.println(key + " not found in query or null!");
					}
				});

				values.add(object);
			}
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the data
	 */
	public Map<String, TableColumnData> getData()
	{
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Map<String, TableColumnData> data)
	{
		this.data = data;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName()
	{
		return tableName;
	}

	/**
	 * @param tableName
	 *            the tableName to set
	 */
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	/**
	 * @return the values
	 */
	public ObservableList<BaseEntity> getValues()
	{
		return values;
	}

	/**
	 * @param values
	 *            the values to set
	 */
	public void setValues(ObservableList<BaseEntity> values)
	{
		this.values = values;
	}

	/**
	 * @return the foreignKeys
	 */
	public Map<String, String> getForeignKeys()
	{
		return foreignKeys;
	}

	/**
	 * @param foreignKeys
	 *            the foreignKeys to set
	 */
	public void setForeignKeys(Map<String, String> foreignKeys)
	{
		this.foreignKeys = foreignKeys;
	}

	/**
	 * @return the selectedEntity
	 */
	public ObjectProperty<BaseEntity> getSelectedEntity()
	{
		return selectedEntity;
	}

	/**
	 * @param selectedEntity
	 *            the selectedEntity to set
	 */
	public void setSelectedEntity(BaseEntity selectedEntity)
	{
		this.selectedEntity.setValue(new BaseEntity(selectedEntity));
	}

}
