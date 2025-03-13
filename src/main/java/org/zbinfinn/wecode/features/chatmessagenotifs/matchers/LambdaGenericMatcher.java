package org.zbinfinn.wecode.features.chatmessagenotifs.matchers;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.features.chatmessagenotifs.Matcher;

import java.util.function.UnaryOperator;

public class LambdaGenericMatcher extends Matcher {
    private String regex;
    private UnaryOperator<String> lambda;
    private double duration;

    public LambdaGenericMatcher(String regex, UnaryOperator<String> lambda, double duration) {
        this.regex = regex;
        this.lambda = lambda;
        this.duration = duration;
    }

    @Override
    public boolean matches(String message) {
        return message.matches(regex);
    }

    @Override
    public Text modify(Text text, String message) {
        return Text.literal(lambda.apply(message));
    }

    public static LambdaGenericMatcher gen(String regex, UnaryOperator<String> lambda) {
        return new LambdaGenericMatcher(regex, lambda, 5);
    }
    public static LambdaGenericMatcher gen(String regex, double duration, UnaryOperator<String> lambda) {
        return new LambdaGenericMatcher(regex, lambda, duration);
    }

    @Override
    public double getDuration() {
        return duration;
    }
}
