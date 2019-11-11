/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.api.http.service.endpoint;

import bisq.api.http.model.OfferDetail;
import bisq.api.http.model.OfferList;

import bisq.core.offer.OfferBookService;

import bisq.common.UserThread;

import javax.inject.Inject;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import static java.util.stream.Collectors.toList;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

@Slf4j
@Tag(name = "offers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OfferEndpoint {

    private final OfferBookService offerBookService;

    @Inject
    public OfferEndpoint(OfferBookService offerBookService) {
        this.offerBookService = offerBookService;
    }

    @Operation(summary = "Find offers", responses = @ApiResponse(content = @Content(schema = @Schema(implementation = OfferList.class))))
    @GET
    public void find(@Suspended AsyncResponse asyncResponse) {
        UserThread.execute(() -> {
            try {
                List<OfferDetail> offers = offerBookService.getOffers().stream().map(OfferDetail::new).collect(toList());
                asyncResponse.resume(new OfferList(offers));
            } catch (Throwable e) {
                asyncResponse.resume(e);
            }
        });
    }

}
