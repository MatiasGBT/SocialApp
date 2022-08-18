import { User } from "./user";

export class Friendship {
    idFriendship: number;
    userTransmitter: User;
    userReceiver: User;
    status: boolean;
    date: Date;
}
