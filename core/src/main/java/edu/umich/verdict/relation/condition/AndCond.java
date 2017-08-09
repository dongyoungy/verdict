package edu.umich.verdict.relation.condition;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import edu.umich.verdict.relation.ExactRelation;

public class AndCond extends Cond {

    private Cond left;

    private Cond right;

    public AndCond(Cond left, Cond right) {
        this.left = left;
        this.right = right;
    }

    public static AndCond from(Cond left, Cond right) {
        return new AndCond(left, right);
    }

    public Cond getLeft() {
        return left;
    }

    public Cond getRight() {
        return right;
    }

    @Override
    public Cond accept(CondModifier v) {
        return from(left.accept(v), right.accept(v));
    }

    @Override
    public String toString() {
        return String.format("(%s) AND (%s)", left, right);
    }

    @Override
    public Pair<Cond, Pair<ExactRelation, ExactRelation>> searchForJoinCondition(List<ExactRelation> tableSources) {
        Pair<Cond, Pair<ExactRelation, ExactRelation>> j = null;
        j = left.searchForJoinCondition(tableSources);
        if (j == null) j = right.searchForJoinCondition(tableSources);
        return j;
    }

    @Override
    public Cond remove(Cond j) {
        if (left.equals(j)) {
            return right;
        } else if (right.equals(j)) {
            return left;
        } else {
            return from(left.remove(j), right.remove(j));
        }

    }

    @Override
    public Cond withTableSubstituted(String newTab) {
        return new AndCond(left.withTableSubstituted(newTab), right.withTableSubstituted(newTab));
    }

    @Override
    public String toSql() {
        return String.format("(%s) AND (%s)", left.toSql(), right.toSql());
    }

    @Override
    public boolean equals(Cond o) {
        if (o instanceof AndCond) {
            return getLeft().equals(((AndCond) o).getLeft()) && getRight().equals(((AndCond) o).getRight());
        }
        return false;
    }
}
