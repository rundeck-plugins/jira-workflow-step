package org.rundeck.plugins.workflow;


import com.dtolabs.rundeck.core.execution.workflow.steps.FailureReason;
import com.dtolabs.rundeck.core.execution.workflow.steps.StepException;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.core.plugins.configuration.PropertyScope;
import com.dtolabs.rundeck.plugins.ServiceNameConstants;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.step.StepPlugin;
import com.dtolabs.rundeck.plugins.step.PluginStepContext;

import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;


import java.util.Map;

/**
 * Jira WorkflowStep Plugin
 */
@Plugin(service = ServiceNameConstants.WorkflowStep, name = "JIRA-Issue-Exists")
@PluginDescription(title = "JIRA Issue Exists", description = "Fail if the specified issue does not exists.")
public class JiraWorkflowStep implements StepPlugin {

    @PluginProperty(name = "issue-key", title = "issue key", description = "Jira issue ID")
    private String issueKey;

    @PluginProperty(name = "url", title = "Server URL", description = "Jira server URL", scope = PropertyScope.Project)
    private String serverURL;

    @PluginProperty(name = "login", title = "login", description = "The account login name", scope = PropertyScope.Project)
    private String login;

    @PluginProperty(name = "password", title = "password", description = "The account password", scope = PropertyScope.Project)
    private String password;

    public JiraWorkflowStep() {

    }

    @Override
    public void executeStep(final PluginStepContext context, final Map<String, Object> configuration)
            throws StepException {
        if (null == login || isBlank(login)) {
            throw new IllegalStateException("login is required");
        }
        if (null == password || isBlank(password)) {
            throw new IllegalStateException("password is required");
        }
        if (null == serverURL || isBlank(serverURL)) {
            throw new IllegalStateException("server URL is required");
        }
        if (null == issueKey) {
            throw new IllegalStateException("issue-key is required");
        }
        /**
         * Connect to JIRA using the configured credentials.
         */
        final BasicCredentials creds = new BasicCredentials(login, password);
        final JiraClient jira = new JiraClient(serverURL, creds);
        try {

            if (!existsIssue(jira, issueKey)) {
                throw new StepException("Issue key \"" + issueKey + "\".", Reason.JiraIssueNotFound);
            }
            /* Retrieve the issue from JIRA */
            Issue issue = jira.getIssue(issueKey);
            /**
             * Print out its description so it can be seen in the execution log.
             * Could do other operations on the issue (get more info, change state, etc)
             */
            System.out.println("Found issue: " +issueKey+", description: "+ issue.getDescription());

        } catch (JiraException je) {

            throw new StepException(je.getMessage(), Reason.JiraException);

        }
    }

    /**
     * Use JQL to search for issue by key.
     *
     * @param jira     The JiraClient
     * @param issueKey The key to search for
     * @return true if one match found. if multiple issues match key, false is returned.
     */
    private boolean existsIssue(final JiraClient jira, String issueKey) {
        Issue.SearchResult results;
        try {
            results = jira.searchIssues("issueKey = " + issueKey);
        } catch (JiraException e) {
            return false;
        }
        return results.total == 1;
    }

    private boolean isBlank(String string) {
        return null == string || "".equals(string);
    }


    static enum Reason implements FailureReason {
        JiraException, JiraIssueNotFound
    }

}
