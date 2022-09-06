import { Comment } from "./comment";
import { Like } from "./like";
import { User } from "./user";

export class Post {
    idPost: number;
    text: string;
    photo: string;
    date: Date;
    user: User;
    likes: Like[];
    comments: Comment[];
}
