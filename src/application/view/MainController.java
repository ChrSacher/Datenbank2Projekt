package application.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Supplier;

import application.TableInfo.TableColumnData;
import application.TableInfo.TableData;
import application.model.BaseEntity;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class MainController
{
	private Connection connection;
	@FXML
	private TabPane tabView;

	private Map<String, TableData> tableData = new TreeMap<String, TableData>();
	String connectionUrl = "jdbc:sqlserver://localhost:1433;" + "database=PZM1;" + "user=SQLTEACHER;"
			+ "password=12345;";

	@FXML
	public void initialize()
	{

		try
		{
			setConnection(DriverManager.getConnection(connectionUrl));
			DatabaseMetaData meta = connection.getMetaData();
			ResultSet resultSet = meta.getTables(null, null, null, new String[] { "TABLE" });

			while (resultSet.next())
			{
				if ("trace_xe_action_map".equals(resultSet.getString(3))
						|| "trace_xe_event_map".equals(resultSet.getString(3))
						|| "sysdiagrams".equals(resultSet.getString(3)))
					continue;
				ResultSet foreignKeys = meta.getImportedKeys(null, null, resultSet.getString(3));
				addNewTable(resultSet.getString(3), foreignKeys);
			}
		}
		// Handle any errors that may have occurred.
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		tableData.forEach((key, value) ->
		{
			addTab(value);
		});
		tabView.tabMinWidthProperty().set(100);// set the tabPane's tabs min and max widths to be the same.
		tabView.tabMaxWidthProperty().set(100);// set the tabPane's tabs min and max widths to be the same.
		tabView.setMinWidth((100 * tabView.getTabs().size()) + 55);// set the tabPane's minWidth and maybe max width to
		// the tabs combined width + a padding value
		tabView.setPrefWidth((100 * tabView.getTabs().size()) + 55);// set the tabPane's minWidth and maybe max width to
		// the tabs combined width + a padding value
	}

	public void addTab(TableData data)
	{

		// Create TAb
		Tab mitTab = new Tab(data.getTableName());
		tabView.getTabs().add(mitTab);

		// Create Grid for layout
		GridPane grid = new GridPane();
		TableView<BaseEntity> table = new TableView();
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(50);
		grid.getColumnConstraints().add(column1);

		int i = 0;
		for (Entry<String, TableColumnData> entry : data.getData().entrySet())
		{

			// Combo box for foreign keys else editbox
			if (data.getForeignKeys().containsKey(entry.getKey()))
			{
				Label label = new Label(entry.getKey());
				grid.add(label, 0, i);

				ComboBox<BaseEntity> combo = new ComboBox();
				ObservableList<BaseEntity> l = tableData.get(data.getForeignKeys().get(entry.getKey())).getValues();
				combo.setItems(l);
				combo.setConverter(new StringConverter<BaseEntity>() {
					@Override
					public String toString(BaseEntity object)
					{
						if(object == null)
						{
							return "NULL";
						}
						return object.getDbEntryValueMap().toString();
					}

					@Override
					public BaseEntity fromString(String string)
					{
						// Somehow pass id and return bank instance
						// If not important, just return null
						return null;
					}
				});
				grid.add(combo, 1, i++);
				
				data.getSelectedEntity().addListener((observableVal, old, newValue) ->
				{
					if (newValue != null)
					{
						
						TableColumnData foreign = entry.getValue();
						System.out.println(foreign.getForeignTable());
						BaseEntity entity = tableData.get(foreign.getForeignTable()).getValues().stream().filter(ent ->
						{
							if(newValue.getValue(entry.getKey()).equals(ent.getValue(foreign.getForeignColumn()))) return true;
							return false;
						}).findFirst().get();
						combo.getSelectionModel().select(entity);
					}
				});
				
				
			} else
			{
				Label label = new Label(entry.getKey());
				grid.add(label, 0, i);

				TextField text = new TextField("");
				grid.add(text, 1, i++);

				// Listener for selecting an a new entity
				data.getSelectedEntity().addListener((observableVal, old, newValue) ->
				{
					if (newValue != null)
					{
						text.setText(newValue.getValue(entry.getKey()));
					}
				});

				// Listener for changing the entity based on values
				text.textProperty().addListener((observable, oldValue, newValue) ->
				{
					if (data.getSelectedEntity() != null)
					{
						data.getSelectedEntity().getValue().setValue(entry.getKey(), newValue);
					}
				});
			}

			// Create TableColumn
			TableColumn<BaseEntity, String> column = new TableColumn(entry.getKey());
			column.setCellValueFactory(
					cellValue -> new SimpleStringProperty(cellValue.getValue().getValue(entry.getKey())));
			table.getColumns().add(column);
		}
		// Set Listener on table for selecting an entity
		table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
		{
			data.setSelectedEntity(newSelection);
		});
		grid.add(table, 0, i, 5, 1);
		mitTab.setContent(grid);
		table.setItems(data.getValues());
	}

	public void addNewTable(String tableName, ResultSet foreignKeys)
	{
		try
		{
			setConnection(DriverManager.getConnection(connectionUrl));

			Statement stmt = getConnection().createStatement();
			String SQL = "SELECT * FROM " + tableName;

			ResultSet rs = stmt.executeQuery(SQL);

			TableData da = new TableData(rs, foreignKeys);
			da.setTableName(tableName);
			tableData.put(tableName, da);

		}
		// Handle any errors that may have occurred.
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public Connection getConnection()
	{
		return connection;
	}

	public void setConnection(Connection connection)
	{
		this.connection = connection;
	}
}
