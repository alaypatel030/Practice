import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.Mailbox;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;

public class outLook {

	public static void main(String[] args) throws Exception {

		// Pro tip: make sure to set your proxy configuration here if needed
		// and exclude outlook.office365.com from proxy SSL inspection.

		String clientId = "";
		String clientSecret = "";
		String tenantName = "";
		String recipientAddr = "";
		String senderAddress = "";

		// I used a ManagedExecutorService provided by glassfish but you can
		// use an ExecutorService and manage it yourself.
		String token = MsEwsTokenProvider.getAccesToken(clientId, clientSecret);
		// don't log this in production!
		System.out.println("token=" + token);

		// test mailbox read access
		System.out.println("geting emails");
		try (ExchangeService service = MsEwsTokenProvider.getAuthenticatedService(token, senderAddress)) {
			listInboxMessages(service, senderAddress);
		}

		// send a message
//		System.out.println("sending a message");
//		try (ExchangeService service = MsEwsTokenProvider.getAuthenticatedService(token, senderAddress)) {
//			sendTestMessage(service, recipientAddr, senderAddress);
//		}
//
//		System.out.println("finished");
	}

	public static void sendTestMessage(ExchangeService service, String recipientAddr, String senderAddr)
			throws Exception {
		EmailMessage msg = new EmailMessage(service);
		msg.setSubject("Hello world!");
		msg.setBody(MessageBody.getMessageBodyFromText("Sent using the EWS Java API."));
		msg.getToRecipients().add(recipientAddr);
		msg.send();
		msg.setSender(new EmailAddress(senderAddr));
	}

	public static void listInboxMessages(ExchangeService service, String mailboxAddr) throws Exception {
		ItemView view = new ItemView(50);
		Mailbox mb = new Mailbox(mailboxAddr);
		FolderId folder = new FolderId(WellKnownFolderName.Inbox, mb);
		FindItemsResults<Item> result = service.findItems(folder, view);
		result.forEach(i -> {
			try {
				System.out.println("subject=" + i.getSubject());
			} catch (ServiceLocalException e) {
				e.printStackTrace();
			}
		});
	}

}