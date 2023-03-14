package bot.util;

public enum Channels {
    REQUIRED_REGISTER_CHANNEL("1084341941984034816"),
    REQUIRED_COUNTDOWN_CHANNEL("1084341941984034816");

    final String channelid;

    Channels(String channelid) { this.channelid = channelid; }

    public String get() { return this.channelid; }
}