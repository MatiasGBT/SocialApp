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
    date: Date;
}
