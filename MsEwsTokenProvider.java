import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.core.util.ExecutorServices;

import com.microsoft.aad.adal4j.AuthenticationCallback;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ConnectingIdType;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.misc.ImpersonatedUserId;

/**
 * Used to obtain an access token for use in an EWS application. Caches the
 * token and refreshes it 5mins prior to expiration.
 * 
 * @author Stephen O'Hair
 *
 */
public final class MsEwsTokenProvider {

	private static final String EWS_URL = "https://outlook.office365.com/EWS/Exchange.asmx";
	private static final String RESOUCE = "https://outlook.office365.com";
	private static final String TENANT_NAME = "";
	private static final String AUTHORITY = "https://login.microsoftonline.com/" + TENANT_NAME + "/v2.0";
	private static final long REFRESH_BEFORE_EXPIRY_MS = Duration.ofMinutes(5).toMillis();

	private static long expiryTimeMs;
	private static String accessToken;

	/**
	 * Takes an OAuth2 token and configures an {@link ExchangeService}.
	 * 
	 * @param token
	 * @param senderAddr
	 * @param traceListener
	 * @param mailboxAddr
	 * @return a configured and authenticated {@link ExchangeService}
	 * @throws URISyntaxException
	 * @throws Exception
	 */
	public static ExchangeService getAuthenticatedService(String token, String senderAddr)
			throws URISyntaxException, Exception {
		ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);

		service.getHttpHeaders().put("Authorization", "Bearer " + token);
		service.getHttpHeaders().put("X-AnchorMailbox", senderAddr);
		// service.setWebProxy(new WebProxy(proxyHost, proxyPort));
		service.setUrl(new URI(EWS_URL));
		service.setImpersonatedUserId(new ImpersonatedUserId(ConnectingIdType.PrincipalName, senderAddr));
		return service;
	}

	/**
	 * Simple way to get an access token using the Azure Active Directory Library.
	 * 
	 * Authenticates at : https://login.microsoftonline.com/
	 * 
	 * @param clientId     - client id of the AzureAD application
	 * @param clientSecret - client secret of the AzureAD application
	 * @param service      - managed executor service
	 * 
	 * @return provisioned access token
	 * @throws MalformedURLException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	public static synchronized String getAccesToken(String clientId, String clientSecret)
			throws MalformedURLException, InterruptedException, ExecutionException, TimeoutException {

		long now = System.currentTimeMillis();
		if (accessToken == null) {

			ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<Runnable>());

			AuthenticationContext context = new AuthenticationContext(AUTHORITY, false, executorService);
			AuthenticationCallback<AuthenticationResult> callback = new AuthenticationCallback<AuthenticationResult>() {

				@Override
				public void onSuccess(AuthenticationResult result) {
					System.out.println("received token");
				}

				@Override
				public void onFailure(Throwable exc) {
					throw new RuntimeException(exc);
				}
			};

			System.out.println("requesting token");
			Future<AuthenticationResult> future = context.acquireToken(RESOUCE,
					new ClientCredential(clientId, clientSecret), callback);

			// wait for access token
			AuthenticationResult result = future.get(30, TimeUnit.SECONDS);

			// cache token and expiration
			accessToken = result.getAccessToken();
			expiryTimeMs = result.getExpiresAfter();
		}

		return accessToken;
	}
}