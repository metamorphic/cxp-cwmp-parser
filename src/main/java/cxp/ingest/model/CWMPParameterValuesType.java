package cxp.ingest.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Iterator;
import javax.annotation.Nullable;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CWMPParameterValuesType {

    protected List<ParameterValue> parameterValues;

    public List<ParameterValue> getParameterValues()
    {
        return parameterValues;
    }

    public void setParameterValues(List<ParameterValue> parameterValues)
    {
        this.parameterValues = parameterValues;
    }

    public String getParameterValue(final String name) {
        if (name == null) return null;
        if (parameterValues == null || parameterValues.isEmpty()) return null;
        Iterator<ParameterValue> filtered = Iterables.filter(parameterValues, new Predicate<ParameterValue>() {
            @Override
            public boolean apply(@Nullable ParameterValue input) {
                return input != null && name.equals(input.getName());
            }
        }).iterator();
        if (filtered.hasNext()) {
            return filtered.next().getValue();
        }
        return null;
    }

    public String getParameterValueByPattern(String regex) {
        if (regex == null) return null;
        if (parameterValues == null || parameterValues.isEmpty()) return null;
        final Pattern p = Pattern.compile(regex);

        Iterator<ParameterValue> filtered = Iterables.filter(parameterValues, new Predicate<ParameterValue>() {
            @Override
            public boolean apply(@Nullable ParameterValue input) {
                Matcher m = p.matcher(input.getName());
                return input != null && m.matches();
            }
        }).iterator();
        if (filtered.hasNext()) {
            return filtered.next().getValue();
        }
        return null;
    }
}
