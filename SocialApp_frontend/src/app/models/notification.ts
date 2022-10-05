import { Comment } from "./comment";
import { Friendship } from "./friendship";
import { Post } from "./post";
import { User } from "./user";

export class Notification {
    idNotification: number;
    isViewed: boolean;
    friend: User;
    userTransmitter: User;
    post: Post;
    friendship: Friendship;
    comment: Comment;
    date: Date;
}
