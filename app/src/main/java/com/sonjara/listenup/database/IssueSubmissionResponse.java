package com.sonjara.listenup.database;

import java.sql.Date;
import java.sql.Timestamp;

public class IssueSubmissionResponse
{
    public int      issue_id;
    public int      safety_issue_id;
    public String   submission_status;
    public Timestamp date_received;
    public String   message;
    public String   details;
}
