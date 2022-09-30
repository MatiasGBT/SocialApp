import { Post } from "./post";
import { ReportReason } from "./report-reason";
import { User } from "./user";

export class Report {
    idReport: number;
    post: Post;
    user: User;
    reportReason: ReportReason;
    extraInformation: string;
}
