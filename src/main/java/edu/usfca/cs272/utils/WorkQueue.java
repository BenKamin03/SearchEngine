package edu.usfca.cs272.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple work queue implementation based on the IBM developerWorks article by
 * Brian Goetz. It is up to the user of this class to keep track of whether
 * there is any pending work remaining.
 *
 * @see <a href=
 *      "https://web.archive.org/web/20210126172022/https://www.ibm.com/developerworks/library/j-jtp0730/index.html">
 *      Java Theory and Practice: Thread Pools and Work Queues</a>
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class WorkQueue {
	/** Workers that wait until work (or tasks) are available. */
	private final Worker[] workers;

	/** Queue of pending work (or tasks). */
	private final LinkedList<Runnable> tasks;
	
	/**
	 * the key for pending
	 */
	private final Object pendingKey = new Object();

	/** Pending work */
	private int pending;

	/** Used to signal the workers should terminate. */
	private volatile boolean shutdown;

	/** The default number of worker threads to use when not specified. */
	public static final int DEFAULT = 5;

	/** Logger used for this class. */
	private static final Logger log = LogManager.getLogger();

	/**
	 * Starts a work queue with the default number of threads.
	 *
	 * @see #WorkQueue(int)
	 */
	public WorkQueue() {
		this(DEFAULT);
	}

	/**
	 * Starts a work queue with the specified number of threads.
	 *
	 * @param threads number of worker threads; should be greater than 1
	 */
	public WorkQueue(int threads) {
		log.info("Created WorkQueue");
		this.tasks = new LinkedList<Runnable>();
		this.pending = 0;
		this.workers = new Worker[threads];
		this.shutdown = false;

		// start the threads so they are waiting in the background
		for (int i = 0; i < threads; i++) {
			workers[i] = new Worker();
			workers[i].start();
		}
	}

	/**
	 * Gets the number of active workers
	 * 
	 * @return the number of active workers
	 */
	public int getActiveWorkers() {
		int count = 0;
		synchronized (workers) {
			for (Worker worker : workers) {
				if (worker.isAlive()) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Gets a list of remaining tasks
	 * 
	 * @return the list of tasks
	 */
	public List<Runnable> getTasksRemaining() {
		synchronized (tasks) {
			return Collections.unmodifiableList(tasks);
		}
	}
	
	/**
	 * Method used for getting the amount of pending work left in the queue
	 * 
	 * @return the pending work
	 */
	public int getPending() {
		synchronized (pendingKey) {
			return pending;
		}
	}

	/**
	 * Adds a work (or task) request to the queue. A worker thread will process this
	 * request when available.
	 *
	 * @param task work request (in the form of a {@link Runnable} object)
	 */
	public void execute(Runnable task) {
		log.debug(System.currentTimeMillis() + " adding task " + tasks.size());
		synchronized (tasks) {
			tasks.addLast(task);
			tasks.notifyAll();
		}
		synchronized (pendingKey) {
			pending++;
		}
	}

	/**
	 * Waits for all pending work (or tasks) to be finished. Does not terminate the
	 * worker threads so that the work queue can continue to be used.
	 */
	public void finish() {
		log.debug(System.currentTimeMillis() + " finishing");
		synchronized (tasks) {
			while (pending != 0) {
				try {
					tasks.wait();
				} catch (InterruptedException e) {
					// Handle interruption if necessary
					log.error("Work queue interrupted while waiting for tasks to finish.");
					log.catching(Level.WARN, e);
					Thread.currentThread().interrupt();
					return; // Exit method on interruption
				}
			}
		}
	}

	/**
	 * Similar to {@link Thread#join()}, waits for all the work to be finished and
	 * the worker threads to terminate. The work queue cannot be reused after this
	 * call completes.
	 */
	public void join() {
		log.debug(System.currentTimeMillis() + " joining");
		try {
			finish();
			shutdown();

			for (Worker worker : workers) {
				worker.join();
			}
		} catch (InterruptedException e) {
			log.error(System.currentTimeMillis() + " Work queue interrupted while joining.");
			log.error(System.currentTimeMillis() + " " + e);
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Asks the queue to shutdown. Any unprocessed work (or tasks) will not be
	 * finished, but threads in-progress will not be interrupted.
	 */
	public void shutdown() {
		// safe to do unsynchronized due to volatile keyword
		shutdown = true;

		synchronized (tasks) {
			tasks.notifyAll();
		}
	}

	/**
	 * Returns the number of worker threads being used by the work queue.
	 *
	 * @return number of worker threads
	 */
	public int size() {
		return workers.length;
	}

	/**
	 * Waits until work (or a task) is available in the work queue. When work is
	 * found, will remove the work from the queue and run it.
	 *
	 * <p>
	 * If a shutdown is detected, will exit instead of grabbing new work from the
	 * queue. These threads will continue running in the background until a shutdown
	 * is requested.
	 */
	private class Worker extends Thread {
		/**
		 * Initializes a worker thread with a custom name.
		 */
		public Worker() {
			setName("Worker" + getName());
		}

		@Override
		public void run() {
			log.debug(System.currentTimeMillis() + " Started Running {}", getName());

			Runnable task = null;

			try {
				while (!shutdown) {
					synchronized (tasks) {
						while (tasks.isEmpty() && !shutdown) {
							tasks.wait();
						}
						if (tasks.size() > 0) {
							task = tasks.removeFirst();
							log.debug("Running Task {}", tasks.size());
						} else {
							continue;
						}
					}

					try {
						task.run();
					} catch (RuntimeException e) {
						log.error(this.getName() + " encountered an exception while running.\n");
					} finally {
						synchronized (pendingKey) {
							if (--pending == 0) {
								synchronized (tasks) {
									tasks.notifyAll();
								}
							}
						}
					}
				}
			} catch (InterruptedException e) {
				log.error(System.currentTimeMillis() + " " + this.getName() + " interrupted while waiting.");
				log.error(System.currentTimeMillis() + " " + e);
				Thread.currentThread().interrupt();
			} catch (NoSuchElementException e) {
				log.error(System.currentTimeMillis() + " " + this.getName() + " No such element exception.");
			}
		}
	}
}