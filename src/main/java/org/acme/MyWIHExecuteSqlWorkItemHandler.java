
package org.acme;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.acme.wih.executesql.model.RequestBeyannameDTO;
import org.acme.wih.executesql.model.Rule;
import org.acme.wih.executesql.service.ReadDataSource;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.core.util.RequiredParameterValidator;
import org.jbpm.process.workitem.core.util.Wid;
import org.jbpm.process.workitem.core.util.WidMavenDepends;
import org.jbpm.process.workitem.core.util.WidParameter;
import org.jbpm.process.workitem.core.util.WidResult;
import org.jbpm.process.workitem.core.util.service.WidAction;
import org.jbpm.process.workitem.core.util.service.WidAuth;
import org.jbpm.process.workitem.core.util.service.WidService;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

@Wid(widfile= "MyWIHExecuteSqlDefinitions.wid", name="MyWIHExecuteSqlDefinitions",
        displayName="WIHExecuteSqlDefinitions",
        defaultHandler="mvel: new org.acme.MyWIHExecuteSqlWorkItemHandler(\"dataSourceName\")",
        documentation = "${artifactId}/index.html",
        category = "${artifactId}",
        icon = "MyWIHExecuteSqlDefinitions.png",
        parameters = {
                @WidParameter(name = "Param1", required = true),
                @WidParameter(name = "Param2" ,required = true),
                @WidParameter(name = "Param3", required = true)
        },
        results = {
                @WidResult(name = "Result", runtimeType = "java.lang.Object")
        },
        mavenDepends = {
                @WidMavenDepends(group = "${groupId}", artifact = "${artifactId}", version = "${version}")
        },
        serviceInfo = @WidService(category = "${name}", description = "${description}",
                keywords = "database,fetch,sql,execute",
                action = @WidAction(title = "Execute SQL statements"),
                authinfo = @WidAuth(required = true, params = {"Data source JNDI"},
                        paramsdescription = {"Data source JNDI"},
                        referencesite = "https://github.com/remzisahbaz")
        )
)
public class MyWIHExecuteSqlWorkItemHandler extends AbstractLogOrThrowWorkItemHandler implements Cacheable {
    private static final Logger logger = LoggerFactory.getLogger(MyWIHExecuteSqlWorkItemHandler.class);
    private static final String RESULT = "Result";
    private static final String DEFAULT_COLUMN_SEPARATOR = ",";
    private DataSource ds;
    private int maxResults;
    private String columnSeparator;

    public MyWIHExecuteSqlWorkItemHandler(String dataSourceName) {
        try {
            this.ds = InitialContext.doLookup(dataSourceName);
        } catch (NamingException e) {
            throw new RuntimeException("Unable to look up data source: " + dataSourceName + " - " + e.getMessage());
        }
    }
    public MyWIHExecuteSqlWorkItemHandler(DataSource ds) {
        this.ds = ds;
    }


    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ReadDataSource readDataSource = new ReadDataSource();
        try {
            RequiredParameterValidator.validate(this.getClass(), workItem);
            Map<String, Object> results = new HashMap<>();
            String Param1 = (String) workItem.getParameter("Param1");
            String Param2 = (String) workItem.getParameter("Param2");
            String Param3 = (String) workItem.getParameter("Param3");
            List<String> lines = new ArrayList<String>();

            /*connect and execute sql*/
            try {
                connection = ds.getConnection();

                if(!connection.isClosed()){

                    /*TODO */
                    System.out.println(result);
                    results.put(RESULT,result);
                }else{
                    results.put(RESULT,"[]");
                }
                manager.completeWorkItem(workItem.getId(),results);
            } finally {
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            manager.completeWorkItem(workItem.getId(), results);
        } catch(Exception e) {
            logger.error(e.getMessage());
            handleException(e);
        }
    }
    // overwrite to implement custom resultset processing
    protected Object processResults(ResultSet resultSet) throws Exception {
        List<String> lines = new ArrayList<>();

        while (resultSet.next()) {
            int columnCount = resultSet.getMetaData().getColumnCount();
            List<String> values = new ArrayList<String>();
            for (int i = 0; i < columnCount; i++) {
                values.add(resultSet.getString(i+1));
            }
            lines.add(values.stream().collect(Collectors.joining(columnSeparator)));
        }

        return lines;
    }
    @Override
    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        // stub
    }
    public void close() {
    }
}


