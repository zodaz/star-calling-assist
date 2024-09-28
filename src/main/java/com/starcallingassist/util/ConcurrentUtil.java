package com.starcallingassist.util;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import lombok.extern.slf4j.Slf4j;

/**
 * Contains various concurrency utils.
 */
@Slf4j
public class ConcurrentUtil
{
	/**
	 * Executes the runnable when the lock is acquired and releases the lock once the runnable finishes.
	 *
	 * @param runnable     The runnable to run.
	 * @param lock         The lock to acquire.
	 * @param skipIfLocked Whether to skip executing the runnable if the lock is being held.
	 */
	public static void acquireLockAndRun(final Lock lock, boolean skipIfLocked, final Runnable runnable)
	{
		if (skipIfLocked && !lock.tryLock())
		{
			return;
		}
		else if (!skipIfLocked)
		{
			lock.lock();
		}

		try
		{
			runnable.run();
		}
		catch (Exception e)
		{
			log.error("Error during locked runnable execution", e);
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * Calls the callable when the lock is acquired and releases the lock once the callable finishes.
	 *
	 * @param callable     The callable to call.
	 * @param lock         The lock to acquire.
	 * @param skipIfLocked Whether to skip executing the callable if the lock is being held.
	 * @return             The result of the callable wrapped in an {@link Optional} object.
	 *                     Empty if callable was skipped, threw or returned null.
	 */
	public static <T> Optional<T> acquireLockAndGet(final Lock lock, boolean skipIfLocked, final Callable<T> callable)
	{
		if (skipIfLocked && !lock.tryLock())
		{
			return Optional.empty();
		}
		else if (!skipIfLocked)
		{
			lock.lock();
		}

		try
		{
			return Optional.ofNullable(callable.call());
		}
		catch (Exception e)
		{
			log.error("Error during locked callable execution", e);
		}
		finally
		{
			lock.unlock();
		}

		return Optional.empty();
	}
}
