package dataxu.intranet.filter;

import dataxu.intranet.model.RandomStringRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CustomRandomHeaderFilterTest {
    @Mock
    private RandomStringRepository randomStringRepository;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    private String headerName = "the name";
    private CustomRandomHeaderFilter filter;

    @Before
    public void beforeEachTest() {
        filter = new CustomRandomHeaderFilter(headerName, randomStringRepository);
    }

    @Test
    public void headerIsAddedToResponse() throws IOException, ServletException {
        String value = "the value";
        given(randomStringRepository.load()).willReturn(value);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).addHeader(headerName, value);
        verify(filterChain).doFilter(request, response);
    }
}
