import { Comment } from "./comment";
import { Followership } from "./followership";
import { Friendship } from "./friendship";
import { Post } from "./post";
import { User } from "./user";

export class Notification {
    idNotification: number;
    isViewed: boolean;
    type: string;
    friend: User;
    userTransmitter: User;
    post: Post;
    friendship: Friendship;
    comment: Comment;
    followership: Followership;
    date: Date;
}
