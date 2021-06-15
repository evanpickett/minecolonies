package com.minecolonies.api.colony.buildings.modules;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.jobs.IJob;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.crafting.IGenericRecipe;
import com.minecolonies.api.crafting.IRecipeStorage;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

/**
 * This module represents the ability for a building to generate
 * items via some form of crafting, whether vanilla, smelter, or
 * some entirely custom process.  It also supports (within some
 * restrictions) automatic JEI integration to let the player look
 * up which items can be produced at each building.
 *
 * A single building may have more than one of these, but in that
 * case they must be linked to different jobs (or to no job) and
 * you have to more carefully manage the learned recipe list.
 *
 * Note that (somewhat uniquely among modules) while there is a
 * "real" one of these attached to the building on the server
 * side that can hold state, additional instances can also be
 * created (with a null building) on the client side in order to
 * answer abstract (but building-type-specific) questions for JEI.
 * When called this way, it must be able to provide answers
 * completely stateless with no expectation that the building
 * (or any colony, for that matter) actually exists.
 */
public interface ICraftingBuildingModule extends IBuildingModule
{
    /**
     * Gets the crafting job associated with this building type.
     * This might not be the primary job of the building.
     *
     * Note that this must either always return null or always
     * return a valid job, even if this module is not currently
     * associated with a specific colony or building.  (Note that
     * if it returns null, there will be no JEI integration.)
     *
     * It may either return the actual job associated with a
     * citizen working at the current building, or an abstract
     * job not yet associated with any particular citizen.
     *
     * Every unique module (across all building types) must return
     * a unique job -- it is not permitted for the same job to be
     * shared by two modules, as that creates an ambiguity.
     *
     * @return The crafting job, or null if there is no such job.
     */
    @Nullable
    IJob<?> getCraftingJob();

    /**
     * Check if this building type can learn (or otherwise process)
     * vanilla crafting recipes of some kind.
     *
     * @return True if so; false otherwise.
     */
    boolean canLearnCraftingRecipes();

    /**
     * Check if this building type can learn (or otherwise process)
     * vanilla smelting recipes of some kind.
     *
     * @return True if so; false otherwise.
     */
    boolean canLearnFurnaceRecipes();

    /**
     * Check if it is possible for this building type to learn
     * recipes that will only fit in a 3x3 crafting grid.
     *
     * @return True if 3x3 recipes can be taught.
     */
    boolean canLearnLargeRecipes();

    /**
     * Checks if this particular recipe is *possible* to be learned by
     * this building (or otherwise possible to be crafted there).
     *
     * This is checked without regard to specific colony or level of
     * building, or whether there are spare recipe slots or not.
     *
     * @param recipe The recipe to check.
     * @return True if this recipe could be produced/taught.
     */
    boolean isRecipeCompatible(@NotNull IGenericRecipe recipe);

    List<IToken<?>> getRecipes();

    @Nullable
    IRecipeStorage getFirstRecipe(ItemStack stack);

    /**
     * Check if is the worker has the knowledge to craft something.
     *
     * @param stackPredicate the predicate to check for fullfillment.
     * @return the recipe storage if so.
     */
    @Nullable
    IRecipeStorage getFirstRecipe(Predicate<ItemStack> stackPredicate);

    /**
     * Get a fullfillable recipe to execute.
     *
     * @param stackPredicate the predicate to check for fullfillment.
     * @param count          the count to produce.
     * @param considerReservation if reservations should be considered.
     * @return the recipe or null.
     */
    IRecipeStorage getFirstFullFillableRecipe(Predicate<ItemStack> stackPredicate, final int count, final boolean considerReservation);

    boolean fullFillRecipe(IRecipeStorage storage);

    /**
     * Replace one current recipe with a new one
     * @param oldRecipe the recipe to replace
     * @param newRecipe the new version
     */
    void replaceRecipe(IToken<?> oldRecipe, IToken<?> newRecipe);

    /**
     * Check if a recipe can be added. This is only important for 3x3 crafting. Workers shall override this if necessary.
     *
     * @param token the token of the recipe.
     * @return true if so.
     */
    boolean canRecipeBeAdded(IToken<?> token);

    /**
     * Add a recipe to the list of recipes.
     *
     * @param token the token to add.
     */
    void addRecipeToList(IToken<?> token, boolean atTop);

    void switchOrder(int i, int j);

    /**
     * Generates any additional special recipes supported by this
     * crafter.  Unlike the above, these are not added to the
     * building's learned recipe list; they are only used for
     * display purposes such as the JEI lookup.
     *
     * This is intended for things that the worker AI can do by
     * itself without any explicit recipe (neither taught nor loaded).
     *
     * @return The list of additional recipes.
     */
    @NotNull
    List<IGenericRecipe> getAdditionalRecipesForDisplayPurposesOnly();

    /**
     * Add a recipe to the building.
     *
     * @param token the id of the recipe.
     * @return true if successful
     */
    boolean addRecipe(IToken<?> token);

    /**
     * Remove a recipe of the building.
     *
     * @param token the id of the recipe.
     */
    void removeRecipe(IToken<?> token);

    /**
     * Check for worker specific recipes and add them if necessary.
     */
    void checkForWorkerSpecificRecipes();

    /**
     * Clear the list of recipes.
     */
    void clearRecipes();

    /**
     * Randomly improve a certain recipe.
     * @param currentRecipeStorage the recipe to improve.
     * @param craftCounter the craft counter.
     * @param citizenData the citizen running it.
     */
    void improveRecipe(IRecipeStorage currentRecipeStorage, int craftCounter, ICitizenData citizenData);
}
