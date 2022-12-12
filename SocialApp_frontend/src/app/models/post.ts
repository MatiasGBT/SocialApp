import { Like } from "./like";
import { Report } from "./report";
import { User } from "./user";

export class Post {
    idPost: number;
    text: string;
    photo: string;
    date: Date;
    user: User;
    likes: Like[];
    reports: Report[];
    isPinned: boolean;
}
