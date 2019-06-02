package application.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import application.Main;
import application.TableInfo.TableColumnData;
import application.TableInfo.TableData;
import application.model.BaseEntity;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

public class MainController
{
	private Connection connection;
	@FXML
	private TabPane tabView;
	@FXML
	private Button logoutButton;
	@FXML
	private Label loggedInLabel;

	private Map<String, TableData> tableData = new TreeMap<String, TableData>();

	public void logout()
	{
		try
		{
			Pane root;
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
			Main.primScene.setRoot(fxmlLoader.load());
			tableData.clear();
			tabView.getTabs().clear();
			try
			{
				connection.close();
			} catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void login(Connection connection, String userName)
	{
		logoutButton.setOnMouseClicked((b) -> logout());
		try
		{
			loggedInLabel.setText(userName);
			setConnection(connection);
			DatabaseMetaData meta = connection.getMetaData();
			ResultSet resultSet = meta.getTables(null, null, null, new String[] { "TABLE" });

			while (resultSet.next())
			{
				String tableName = resultSet.getString(3);

				if ("trace_xe_action_map".equals(tableName) || "trace_xe_event_map".equals(resultSet.getString(3)) || "sysdiagrams".equals(resultSet.getString(3)))
					continue;
				ResultSet foreignKeys = meta.getImportedKeys(null, null, tableName);
				ResultSet primaryKeys = meta.getPrimaryKeys(null, null, tableName);
				addNewTable(tableName, primaryKeys, foreignKeys);
			}
		}

		// Handle any errors that may have occurred.
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		tableData.forEach((key, value) -> {
			addTab(value);

		});
		tabView.tabMinWidthProperty().set(100);// set the tabPane's tabs min and max widths to be the same.
		tabView.tabMaxWidthProperty().set(100);// set the tabPane's tabs min and max widths to be the same.
		tabView.setMinWidth((100 * tabView.getTabs().size()) + 55);// set the tabPane's minWidth and maybe max width to
		// the tabs combined width + a padding value
		tabView.setPrefWidth((100 * tabView.getTabs().size()) + 55);// set the tabPane's minWidth and maybe max width to
		// the tabs combined width + a padding value
	}

	/**
	 * Create and add a Tab to the TabView
	 * 
	 * @param data
	 */
	public void addTab(TableData data)
	{

		// Create TAb
		Tab mitTab = new Tab(data.getTableName());
		tabView.getTabs().add(mitTab);

		// Create Grid for layout
		GridPane grid = new GridPane();
		TableView<BaseEntity> table = new TableView();
		table.setMaxWidth(Double.MAX_VALUE);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setHgrow(Priority.ALWAYS);
		column1.setPercentWidth(50);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setHgrow(Priority.ALWAYS);
		column2.setPercentWidth(50);
		grid.getColumnConstraints().add(column1);
		grid.getColumnConstraints().add(column2);
		int i = 0;
		for (Entry<String, TableColumnData> entry : data.getData().entrySet())
		{

			// Combo box for foreign keys else editbox
			if (data.getForeignKeys().containsKey(entry.getKey()))
			{
				Label label = new Label(entry.getKey());
				grid.add(label, 0, i);

				ComboBox<BaseEntity> combo = new ComboBox();
				combo.setMaxWidth(Double.MAX_VALUE);
				ObservableList<BaseEntity> l = tableData.get(data.getForeignKeys().get(entry.getKey())).getValues();
				combo.setItems(l);
				combo.setConverter(new StringConverter<BaseEntity>()
				{
					@Override
					public String toString(BaseEntity object)
					{
						if (object == null)
						{
							return "NULL";
						}
						// return object.getValue(enty.getKey());
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

				data.getSelectedEntity().addListener((observableVal, old, newValue) -> {
					if (newValue != null)
					{

						TableColumnData foreign = entry.getValue();
						System.out.println(foreign.getForeignTable());
						BaseEntity entity = tableData.get(foreign.getForeignTable()).getValues().stream().filter(ent -> {
							if (newValue.getValue(entry.getKey()).equals(ent.getValue(foreign.getForeignColumn())))
								return true;
							return false;
						}).findFirst().get();
						combo.getSelectionModel().select(entity);
					}
				});

			} else
			{
				Label label = new Label(entry.getKey());
				grid.add(label, 0, i);
				label.setMaxWidth(Double.MAX_VALUE);
				TextField text = new TextField("");
				grid.add(text, 1, i++);

				// Listener for selecting an a new entity
				data.getSelectedEntity().addListener((observableVal, old, newValue) -> {
					if (newValue != null)
					{
						text.setText(newValue.getValue(entry.getKey()));
					}
				});

				// Listener for changing the entity based on values
				text.textProperty().addListener((observable, oldValue, newValue) -> {
					if (data.getSelectedEntity().get() != null)
					{
						data.getSelectedEntity().getValue().setValue(entry.getKey(), newValue);
					}
				});
			}

			// Create TableColumn
			TableColumn<BaseEntity, String> column = new TableColumn(entry.getKey());
			column.setCellValueFactory(cellValue -> new SimpleStringProperty(cellValue.getValue().getValue(entry.getKey())));
			table.getColumns().add(column);

		}
		// Set Listener on table for selecting an entity
		table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null)
			{
				data.setSelectedEntity(newSelection);
			}
		});
		grid.add(table, 0, i, 5, 1);

		// SAVE BUTTON
		addSaveButton(data, grid, i);

		mitTab.setContent(grid);
		table.setItems(data.getValues());
	}

	public void addNewTable(String tableName, ResultSet primaryKeys, ResultSet foreignKeys)
	{
		try
		{

			Statement stmt = getConnection().createStatement();
			String SQL = "SELECT * FROM " + tableName;

			ResultSet rs = stmt.executeQuery(SQL);

			TableData da = new TableData(rs, primaryKeys, foreignKeys);
			da.setTableName(tableName);
			tableData.put(tableName, da);
			da.setSelectedEntity(new BaseEntity());

		}
		// Handle any errors that may have occurred.
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void addSaveButton(TableData data, GridPane grid, int i)
	{
		Button saveButton = new Button();

		saveButton.setText("Save Changes");
		grid.add(saveButton, 0, i + 1);

		saveButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent e)
			{
				try
				{
					System.out.println("SAVE BUTTON: " + data.getSelectedEntity());
					if (data.getSelectedEntity().get() != null)
					{

						List<String> primaryKeys = new ArrayList<>();
						primaryKeys.addAll(data.getPrimaryKeys());

						String tableName = data.getTableName();
						String whatToUpdate = "";
						String whereToUpdate = "";
						List<Entry<String, String>> primaryEntries = new ArrayList<Map.Entry<String, String>>();

						for (Iterator<Entry<String, String>> it = data.getSelectedEntity().get().getDbEntryValueMap().entrySet().iterator(); it.hasNext();)
						{
							Entry<String, String> entry = it.next();
							if (it.hasNext())
							{
								whatToUpdate += entry.getKey() + "='" + entry.getValue() + "', ";

							} else
							{
								whatToUpdate += entry.getKey() + "='" + entry.getValue() + "'";

							}
							if (primaryKeys.contains(entry.getKey()))
							{
								primaryEntries.add(entry);
							}

						}

						for (Iterator<Entry<String, String>> it = primaryEntries.iterator(); it.hasNext();)
						{
							Entry<String, String> entry = it.next();
							if (it.hasNext())
							{
								whereToUpdate += entry.getKey() + "='" + entry.getValue() + "' AND ";
							} else
							{
								whereToUpdate += entry.getKey() + "='" + entry.getValue() + "'";
							}
						}

						String QUERY = "UPDATE " + tableName + " SET " + whatToUpdate + " WHERE " + whereToUpdate;
						System.out.println("TABLE NAME: " + tableName);
						System.out.println("WHAT TO UPDATE: " + whatToUpdate);
						System.out.println("WHERE TO UPDATE: " + whereToUpdate);
						System.out.println("WHOLE QUERY: " + QUERY);

						PreparedStatement statement = getConnection().prepareStatement(QUERY);

						int rowsUpdated = statement.executeUpdate();
						if (rowsUpdated > 0)
						{
							System.out.println("UPDATE ERFOLGREICH, ROWS UPDATED: " + rowsUpdated);
						} else
						{
							Optional<ButtonType> buttonOpt = showAlert("Datensatz nicht gefunden", "Neuen Datensatz anlegen?",
									"Datensatz mit angegebenen Primärschlüsseln nicht gefunden. Wollen Sie einen neuen Datensatz erstellen?");
							if (buttonOpt.isPresent())
							{
								ButtonType buttonType = buttonOpt.get();
								if (buttonType == ButtonType.OK)
								{
									String keys = "";
									String values = "";
									for (Iterator<Entry<String, String>> it = data.getSelectedEntity().get().getDbEntryValueMap().entrySet().iterator(); it.hasNext();)
									{
										Entry<String, String> entry = it.next();
										if (it.hasNext())
										{
											keys += entry.getKey() + ",";
											values += "'" + entry.getValue() + "',";
										} else
										{
											keys += entry.getKey();
											values += "'" + entry.getValue() + "'";
										}
									}
									QUERY = "INSERT INTO " + tableName + " (" + keys + ") " + " VALUES (" + values + ")";
									System.out.println("QUERY INSERT: " + QUERY);
									statement = getConnection().prepareStatement(QUERY);
									statement.execute();

								}
							}
						}
						Statement stmt = getConnection().createStatement();
						String SQL = "SELECT * FROM " + data.getTableName();

						ResultSet rs = stmt.executeQuery(SQL);
						data.loadValues(rs);
					}

				} catch (SQLException ex)
				{
					ex.printStackTrace();
				}

			}
		});

	}

	private Optional<ButtonType> showAlert(String title, String header, String content)
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);

		return alert.showAndWait();
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
