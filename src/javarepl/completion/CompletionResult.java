package javarepl.completion;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.json.Json;

import java.util.List;
import java.util.Map;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.collections.PersistentMap.constructors.emptyMap;
import static com.googlecode.totallylazy.collections.PersistentMap.constructors.map;
import static com.googlecode.totallylazy.json.Json.json;


public class CompletionResult {
    private final String expression;
    private final Integer position;
    private final Sequence<CompletionCandidate> candidates;

    public CompletionResult(String expression, Integer position, Sequence<CompletionCandidate> candidates) {
        this.expression = expression;
        this.position = position;
        this.candidates = candidates;
    }

    public Integer position() {
        return position;
    }

    public String expression() {
        return expression;
    }

    public Sequence<CompletionCandidate> candidates() {
        return candidates;
    }

    @Override
    public String toString() {
        return expression + " -> " + candidates.toString("[", ",", "]") + " @ " + position;
    }

    @Override
    public int hashCode() {
        return (expression != null ? expression.hashCode() : 0) +
                (candidates != null ? candidates.hashCode() : 0) +
                (position != null ? position.hashCode() : 0);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof CompletionResult &&
                other.getClass().equals(getClass()) &&
                (expression != null && expression.equals(((CompletionResult) other).expression)) &&
                (candidates != null && candidates.equals(((CompletionResult) other).candidates)) &&
                (position != null && position.equals(((CompletionResult) other).position));
    }


    public static final class methods {
        public static String toJson(CompletionResult result) {
            return json(emptyMap(String.class, Object.class)
                    .insert("expression", result.expression())
                    .insert("position", result.position().toString())
                    .insert("candidates", result.candidates().map(completionCandidate -> emptyMap(String.class, Object.class)
                            .insert("value", completionCandidate.value())
                            .insert("forms", completionCandidate.forms().toList())).toList()));
        }

        public static CompletionResult fromJson(String json) {
            Map<String, Object> model = map(Json.map(json));
            return new CompletionResult(model.get("expression").toString(),
                    Integer.valueOf(model.get("position").toString()),
                    sequence((List<Map<String, Object>>)model.get("candidates"))
                            .map(model1 -> new CompletionCandidate(model1.get("value").toString(), sequence((List<String>)model1.get("forms")))));
        }
    }
}
