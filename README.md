
## Description

This WorkflowStep plugin checks for the existence of a Jira issue and fails the job if not present.

Big props to [rcarz](https://github.com/rcarz) and his fantabulous
[jira-client](https://github.com/rcarz/jira-client) that made this plugin possible.


## Build / Deploy

To build the project from source, run: `gradle build`.
The resulting jar will be found in `build/libs`.

Copy the  jar to Rundeck plugins directory. For example, on an RPM installation:

    cp build/libs/jira-workflow-step-1.0.0.jar /var/lib/rundeck/libext

or for a launcher:

    cp build/libs/jira-workflow-step-1.0.0.jar $RDECK_BASE/libext

Then restart the Rundeck service.

## Configuration

The Jira connection credentials are set in the project.properties file
for your project.

```
project.plugin.WorkflowStep.JIRA-Issue-Exists.login=slomo
project.plugin.WorkflowStep.JIRA-Issue-Exists.password=s1inky
project.plugin.WorkflowStep.JIRA-Issue-Exists.url=https://myOnDemand.atlassian.net
```

## Usage

To use the plugin, configure your job to use the workflow step.

The plugin has one input option:

* issue: The JIRA issue ID.

## Example

The example job below checks for the specified JIRA issue and then runs an echo command.
Note, the JIRA issue ID is passed as a job option:

```YAML
- id: 1eb09968-6390-441f-be8e-6649eeba581c
  name: jira workflow step example
  project: examples
  description: 'fail the job if the specified issue is not found '
  loglevel: INFO
  sequence:
    keepgoing: false
    strategy: node-first
    commands:
    - type: JIRA-Issue-Exists
      nodeStep: false
      configuration:
        issue key: ${option.jira_issue}
    - exec: echo hi
  uuid: 1eb09968-6390-441f-be8e-6649eeba581c
  options:
    jira_issue:
      description: the jira issue to check
```

## Troubleshooting

Errors from JIRA communication can be found in Rundeck's service.log.
