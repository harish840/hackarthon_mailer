package com.smartmailer.reader;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;

import com.smartmailer.email.Email;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

public class GmailIncomingTest {

	private static final String username = "ashish.gupta03@snapdeal.com";
	private static final String password = "king_kong123";

	public static void main(String[] args) {

		Properties properties = new Properties();
		// properties.put("mail.debug", "true");
		properties.put("mail.store.protocol", "imaps");
		properties.put("mail.imaps.host", "imap.gmail.com");
		properties.put("mail.imaps.port", "993");
		properties.put("mail.imaps.timeout", "10000");

		Session session = Session.getInstance(properties); // not
															// getDefaultInstance
		IMAPStore store = null;
		Folder inbox = null;

		try {
			store = (IMAPStore) session.getStore("imaps");
			store.connect(username, password);

			if (!store.hasCapability("IDLE")) {
				throw new RuntimeException("IDLE not supported");
			}

			inbox = (IMAPFolder) store.getFolder("INBOX");
			inbox.addMessageCountListener(new MessageCountAdapter() {

				@Override
				public void messagesAdded(MessageCountEvent event) {
					Message[] messages = event.getMessages();

					for (Message message : messages) {

						try {
							Email email = new Email();
							Multipart mp = (Multipart) message.getContent();
							email.setBody(mp.getBodyPart(0).getContent()
									.toString());
							email.setRecipient(message.getAllRecipients()[0]
									.toString());
							email.setFromDate(message.getSentDate().toString());
							email.setSubject(message.getSubject());
							System.out.println(email.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});

			IdleThread idleThread = new IdleThread(inbox);
			idleThread.setDaemon(false);
			idleThread.start();

			idleThread.join();
			// idleThread.kill(); //to terminate from another thread

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			close(inbox);
			close(store);
		}
	}

	private static class IdleThread extends Thread {
		private final Folder folder;
		private volatile boolean running = true;

		public IdleThread(Folder folder) {
			super();
			this.folder = folder;
		}

		public synchronized void kill() {

			if (!running)
				return;
			this.running = false;
		}

		@Override
		public void run() {
			while (running) {

				try {
					ensureOpen(folder);
					System.out.println("enter idle");
					((IMAPFolder) folder).idle();
				} catch (Exception e) {
					// something went wrong
					// wait and try again
					e.printStackTrace();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// ignore
					}
				}

			}
		}
	}

	public static void close(final Folder folder) {
		try {
			if (folder != null && folder.isOpen()) {
				folder.close(false);
			}
		} catch (final Exception e) {
			// ignore
		}

	}

	public static void close(final Store store) {
		try {
			if (store != null && store.isConnected()) {
				store.close();
			}
		} catch (final Exception e) {
			// ignore
		}

	}

	public static void ensureOpen(final Folder folder)
			throws MessagingException {

		if (folder != null) {
			Store store = folder.getStore();
			if (store != null && !store.isConnected()) {
				store.connect(username, password);
			}
		} else {
			throw new MessagingException("Unable to open a null folder");
		}

		if (folder.exists() && !folder.isOpen()
				&& (folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
			System.out.println("open folder " + folder.getFullName());
			folder.open(Folder.READ_ONLY);
			if (!folder.isOpen())
				throw new MessagingException("Unable to open folder "
						+ folder.getFullName());
		}

	}
}
