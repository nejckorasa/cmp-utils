package io.github.nejckorasa.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static io.github.nejckorasa.result.Diff.ADDED;
import static io.github.nejckorasa.result.Diff.REMOVED;
import static io.github.nejckorasa.result.Diff.UNCHANGED;
import static io.github.nejckorasa.result.Diff.UPDATED;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class CmpResult<B, W> {
    private final List<CmpPair<B, W>> removed = new ArrayList<>();
    private final List<CmpPair<B, W>> added = new ArrayList<>();
    private final List<CmpPair<B, W>> updated = new ArrayList<>();
    private final List<CmpPair<B, W>> unchanged = new ArrayList<>();
    private final int changesCount;
    private final int differentCount;

    public CmpResult(List<CmpPair<B, W>> removed, List<CmpPair<B, W>> added, List<CmpPair<B, W>> updated, List<CmpPair<B, W>> unchanged) {
        this.removed.addAll(removed);
        this.added.addAll(added);
        this.updated.addAll(updated);
        this.unchanged.addAll(unchanged);
        changesCount = removed.size() + added.size() + updated.size();
        differentCount = removed.size() + added.size();
    }

    /**
     * Gets all compare pairs, changed and unchanged
     *
     * @return list of compare pairs
     */
    public List<CmpPair<B, W>> getAll() {
        return stream().collect(toList());
    }

    /**
     * Gets all compare pairs of items removed from base collection
     *
     * @return list of compare pairs
     */
    public List<CmpPair<B, W>> getRemoved() {
        return removed;
    }

    /**
     * Gets all compare pairs of items added to working collection
     *
     * @return list of compare pairs
     */
    public List<CmpPair<B, W>> getAdded() {
        return added;
    }

    /**
     * Gets all compare pairs of items that exist in both collections but were updated.
     * Updated means changed based on equals function.
     *
     * @return list of compare pairs
     */
    public List<CmpPair<B, W>> getUpdated() {
        return updated;
    }

    /**
     * Gets all compare pairs of unchanged items
     *
     * @return list of compare pairs
     */
    public List<CmpPair<B, W>> getUnchanged() {
        return unchanged;
    }

    /**
     * Gets all compare pairs of changed items
     *
     * @return list of compare pairs
     */
    public List<CmpPair<B, W>> getChanged() {
        return streamChanged().collect(toList());
    }

    /**
     * Gets all compare pairs different items - items removed from base collection or added to working collection
     *
     * @return list of compare pairs
     */
    public List<CmpPair<B, W>> getDifferent() {
        return streamDifferent().collect(toList());
    }

    /**
     * Gets all items added to working collection
     *
     * @return list of added items
     */
    public List<W> getAddedItems() {
        return added.stream().map(CmpPair::getWorking).collect(toList());
    }

    /**
     * Gets all items removed from base collection
     *
     * @return list of removed items
     */
    public List<B> getRemovedItems() {
        return removed.stream().map(CmpPair::getBase).collect(toList());
    }

    /**
     * Gets all items removed from base collection or added to working collection
     *
     * @return list different items
     */
    public List<Object> getDifferentItems() {
        return streamDifferent().map(CmpPair::getLatest).collect(toList());
    }

    /**
     * Gets number of changes. Sum of removed, added, updated items.
     *
     * @return changes count
     */
    public int getChangesCount() {
        return changesCount;
    }

    /**
     * @return true if changes exist
     */
    public boolean hasChanges() {
        return changesCount > 0;
    }

    /**
     * Gets number of different items = items removed from base collection or added to working collection.
     *
     * @return number of different items
     * @see #getDifferent()
     */
    public int getDifferentCount() {
        return differentCount;
    }

    /**
     * @return true if differences exist
     * @see #getDifferent()
     */
    public boolean hasDifferences() {
        return differentCount > 0;
    }

    /**
     * Performs action on each compare pair
     *
     * @param action action to perform
     */
    public void forEach(final Consumer<CmpPair<B, W>> action) {
        removed.forEach(action);
        added.forEach(action);
        updated.forEach(action);
        unchanged.forEach(action);
    }

    /**
     * Performs action on each changed compare pair
     *
     * @param action action to perform
     */
    public void forEachChanged(final Consumer<CmpPair<B, W>> action) {
        removed.forEach(action);
        added.forEach(action);
        updated.forEach(action);
    }

    /**
     * Performs action on each unchanged compare pair
     *
     * @param action action to perform
     */
    public void forEachUnchanged(final Consumer<CmpPair<B, W>> action) {
        unchanged.forEach(action);
    }

    /**
     * Streams all compare pairs
     *
     * @return stream
     */
    public Stream<CmpPair<B, W>> stream() {
        return Stream.of(removed, added, updated, unchanged).flatMap(Collection::stream);
    }

    /**
     * Streams all changed compare pairs
     *
     * @return stream
     */
    public Stream<CmpPair<B, W>> streamChanged() {
        return Stream.of(removed, added, updated).flatMap(Collection::stream);
    }

    /**
     * Streams all unchanged compare pairs
     *
     * @return stream
     */
    public Stream<CmpPair<B, W>> streamUnchanged() {
        return Stream.of(unchanged).flatMap(Collection::stream);
    }

    /**
     * Streams all different compare pairs
     *
     * @return stream
     * @see #getDifferent()
     */
    public Stream<CmpPair<B, W>> streamDifferent() {
        return Stream.of(removed, added).flatMap(Collection::stream);
    }

    /**
     * @return compare result as a map with {@link Diff} as keys
     */
    public Map<Diff, Collection<CmpPair<B, W>>> asMap() {
        return Map.of(ADDED, added, UPDATED, updated, REMOVED, removed, UNCHANGED, unchanged);
    }

    @Override
    public String toString() {
        return format("CmpResult{removed=%s, added=%s, updated=%s, unchanged=%s}", removed, added, updated, unchanged);
    }
}

