package aigm.workflow;

public record DowntimeActivityChoice(
    Kind kind,
    String pcId,
    String details,
    boolean extraPaidWithCoin
) {

    public enum Kind {
        ACQUIRE_ASSET,
        LONG_TERM_PROJECT,
        RECOVER,
        REDUCE_HEAT,
        TRAIN,
        INDULGE_VICE
    }
}
