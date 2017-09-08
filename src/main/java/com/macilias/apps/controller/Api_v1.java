package com.macilias.apps.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.macilias.apps.model.*;
import com.macilias.apps.service.crowdtangle.Client;
import com.macilias.apps.service.crowdtangle.Service;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.*;
import java.io.IOException;
import java.util.*;


/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public class Api_v1 implements Filter {

    private static final Logger LOG = Logger.getLogger(Api_v1.class);
    private static final String INTENT = "Intent";
    private final History history = new History();
    private Service service;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        service = new Service(new Client(Settings.CROWD_TANGLE_API_TOKEN), Settings.CROWD_TANGLE_LIST_ID);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        LOG.info("request(): " + servletRequest.getLocalAddr());
        Set<String> arguments = ArgumentName.getLowerValues();
        Request request = new Request();
        Map m = servletRequest.getParameterMap();
        Set s = m.entrySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {

            Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) it.next();

            String key = entry.getKey().toLowerCase();
            String[] values = entry.getValue();

            LOG.info("key: " + key + " values.length: " + values.length + " first value is: " + values[0]);
            if (INTENT.equalsIgnoreCase(key) && StringUtils.isNoneBlank(values[0])) {
                request.setIntent(Intent.fromValue(values[0]));
            } else {
                if (arguments.contains(key)) {
                    request.addArgument(ArgumentName.fromValue(key), values);
                } else {
                    LOG.warn("Sorry, I don't know what to do with it [" + key + "], its not a Intent and not a Argument");
                }
            }

        }

        Response response = createResponse(request);
        request.setResponse(response);
        history.addRequest(request);

        Gson gson = new GsonBuilder().create();

        servletResponse.setContentType("application/json");
        String responseAsJSON = gson.toJson(response);
        String requestAsString = request.toString();
        String responseAsString = response.toString();
        servletResponse.getOutputStream().println(responseAsJSON);

        LOG.info("request  as String: " + requestAsString);
        LOG.info("response as String: " + responseAsString);
        LOG.info("response as JSON:   " + responseAsJSON);
    }


    /**
     * Intents: FOLLOWER_COUNT, GET_COMMENTS, GET_NEW, POST
     * Argument:
     * All 4 has optional SINCE YYYY-MM-DD otherwise SINCE is equals to last time checked.
     * The FOLLOWER_COUNT, GET_COMMENTS & GET_NEWS might have a optional Argument WHERE that means do it on all
     * POST has 2 Arguments: “WHERE” and “WHAT”
     *
     * @param request
     * @return
     */
    private Response createResponse(Request request) throws IOException {
        if (request.getIntent() == null) {
            return new Response("Sorry, I was not able to understand your intention.");
        }
        Optional<String> whereIdentifier = getOptionalArgumentValue(request, ArgumentName.WHERE);
        Optional<String> sinceIdentifier = getOptionalArgumentValue(request, ArgumentName.SINCE);
        int thisKindOfResponseCount = getThisKindOfResponseCount(request);
        request.addArgument(ArgumentName.FAKED_UPDATE, String.valueOf(thisKindOfResponseCount));
        switch (request.getIntent()) {
            case UPDATE:
                Optional<String> whereSpecifier = whereIdentifier.isPresent() && StringUtils.isNoneBlank(whereIdentifier.get()) ? Optional.of("on " + whereIdentifier.get()) : Optional.empty();
                String updateResponseText = "";
                if (thisKindOfResponseCount % 3 == 0) {
                    updateResponseText = "You lost an important follower " + whereSpecifier.orElse("") + " - Steve Gates but hey, there are 10 new ones.";
                } else if (thisKindOfResponseCount % 3 == 1) {
                    updateResponseText = "Oh no, now Elon Wonzniak has left you " + whereSpecifier.orElse("") + " too, you really should be more careful about your " +
                            (whereIdentifier.isPresent() ? (whereIdentifier.get().equalsIgnoreCase("twitter") ? "tweets" : "posts") : "tweets") + " Donald.";
                } else if (thisKindOfResponseCount % 3 == 2) {
                    updateResponseText = "You managed not too loose important followers since the last time, well done. And I got even more good news for you. "
                            + "The serial you tweeted about lately - House of Sticks - has lunched a new season on Netflix. Should I cancel your meetings for today?";
                }
                return new Response(updateResponseText);
            case FOLLOWER_COUNT:

                int followerCount;
                if (!whereIdentifier.isPresent()) {
                    followerCount = service.followerCount(whereIdentifier, sinceIdentifier);
                } else {
                    Random random = new Random();
                    followerCount = random.nextInt(10000000);
                }
                String followerCountResponseText = getFollowerCountResponseText(followerCount, thisKindOfResponseCount, whereIdentifier, sinceIdentifier);
                Response response = new Response(followerCountResponseText);
                response.addResponseArgument(new ResponseArgument(ResponseArgumentName.COUNT, String.valueOf(followerCount)));
                return response;
            case GET_COMMENTS:
                return new Response("Get comments will be implemented soon, keep the fingers crossed.");
            case GET_NEW:
                return new Response("Get new stuff is not implemented yet, he can´t wait to kick it of now.");
            case POST:
                return new Response("Post something is not implemented yet, but it should work directly with our node.js client.");
        }
        return new Response("Oh, I don´t have a clu what you how to help you right now.");
    }

    private int getThisKindOfResponseCount(Request request) {
        int thisKindOfResponseCount = 0;
        Optional<Request> lastRequestByIntent = history.getLastRequestByIntent(request.getIntent(), Optional.of(ArgumentName.FAKED_UPDATE));
        if (lastRequestByIntent.isPresent()) {
            LOG.info("last request has been found checking for optional argument");
            Optional<Argument> optionalArgument = lastRequestByIntent.get().getOptionalArgument(ArgumentName.FAKED_UPDATE);
            if (optionalArgument.isPresent()) {
                Argument argument = optionalArgument.get();
                thisKindOfResponseCount = Integer.valueOf(argument.getArgumentValues().get(0));
                thisKindOfResponseCount++;
                LOG.info("raising thisKindOfResponseCount was " + argument.getArgumentValues().get(0) + " now its " + thisKindOfResponseCount);
            } else {
                LOG.info("not raising thisKindOfResponseCount - no optional argument found");
            }
        } else {
            LOG.info("no last request found for " + Intent.UPDATE + " and " + ArgumentName.FAKED_UPDATE);
        }
        return thisKindOfResponseCount;
    }

    private String getFollowerCountResponseText(int count, int variation, Optional<String> whereIdentifier, Optional<String> sinceIdentifier) {
        Optional<String> whereSpecifier = whereIdentifier.isPresent() && StringUtils.isNoneBlank(whereIdentifier.get()) ? Optional.of(" on " + whereIdentifier.get()) : Optional.empty();
        Optional<String> sinceSpecifier = sinceIdentifier.isPresent() && StringUtils.isNoneBlank(sinceIdentifier.get()) ? Optional.of(" since " + sinceIdentifier.get()) : Optional.empty();
        Optional<Request> lastRequestByIntent = history.getLastRequestByIntent(Intent.FOLLOWER_COUNT, Optional.of(ArgumentName.WHERE));
        String responseText;
        Optional<Integer> lastCountOptional = getLastCount(lastRequestByIntent);
        LOG.info("getFollowerCountResponseText(): its " + variation + "th variation" + (lastCountOptional.isPresent() ? " - last count was " + lastCountOptional.get() : ""));
        switch (variation % 3) {
            case 0:
                responseText = "Your follower count" + whereSpecifier.orElse("") + " is " +
                        count + " right now" + (whereSpecifier.isPresent() ? "" : " according to crowdtangle") + ".";
                if (lastCountOptional.isPresent()) {
                    int lastCount = lastCountOptional.get();
                    int significat = Math.abs((int) ((count * 100.0f) / lastCount));
                    if (lastCount < count) {
                        if (significat < 30) {
                            responseText += " You have " + (count - lastCount) + " new followers now.";
                        } else if (significat < 60) {
                            responseText += " Wow - you have " + (count - lastCount) + " new followers now. That's quite a boost.";
                        } else {
                            responseText += " Awesome - you have " + (count - lastCount) + " new followers now. How have you done it? Its " + significat + " percent more!";
                        }
                    } else if (lastCount > count) {
                        if (significat < 30) {
                            responseText += " Sorry, you lost " + (lastCount - count) + " followers.";
                        } else if (significat < 60) {
                            responseText += " Oh man - you lost " + (count - lastCount) + " of them, puh, its a bummer. Its " + significat + " percent less!";
                        } else {
                            responseText += " Hell - you don't wanna know it,... ok, well, it's roughly " + significat + " percent less!";
                        }

                    } else {
                        responseText += " That's the same amount you had last time I checked.";
                    }
                }
                break;
            case 1:
                responseText = whereSpecifier.orElse("") + " it's " +
                        count + (whereSpecifier.isPresent() ? "" : " according to crowdtangle") + ".";
                if (lastCountOptional.isPresent()) {
                    int lastCount = lastCountOptional.get();
                    int significat = Math.abs((int) ((count * 100.0f) / lastCount));
                    if (lastCount < count) {
                        if (significat < 80) {
                            responseText += " It's " + (count - lastCount) + " more, congratz.";
                        } else {
                            responseText += " WOW It's " + significat + " percent more, congratz maestro!";
                        }
                    } else if (lastCount > count) {
                        if (significat < 50) {
                            responseText += " It's " + (lastCount - count) + " less.";
                        } else {
                            responseText += " Oh no, It's " + significat + " percent less. How could it happen.";
                        }
                    } else {
                        responseText += " It's exactly the same last time I checked.";
                    }
                }
                break;
            case 2:
                responseText = "Really, again? Ok, " + whereSpecifier.orElse("") + " you've got " +
                        count + (whereSpecifier.isPresent() ? "" : " according to crowdtangle") + ".";
                if (lastCountOptional.isPresent()) {
                    int lastCount = lastCountOptional.get();
                    int significat = Math.abs((int) ((count * 100.0f) / lastCount));
                    if (lastCount < count) {
                        if (significat < 10) {
                            responseText += " At least " + (count - lastCount) + " more.";
                        } else {
                            responseText += " Cool, its " + significat + " percent more.";
                        }
                    } else if (lastCount > count) {
                        if (significat < 10) {
                            responseText += " Well, " + (lastCount - count) + " has terminated their loyalty.";
                        } else {
                            responseText += " Oh no, " + significat + " percent has terminated their loyalty.";
                        }
                    } else {
                        responseText += " Boring, no changes.";
                    }
                }
                break;
            default:
                responseText = "I got no new variations for you man";

        }
        return responseText;
    }

    private Optional<Integer> getLastCount(Optional<Request> lastRequestByIntent) {
        // check if what was the last result of this query
        if (lastRequestByIntent.isPresent()) {
            Request historyRequest = lastRequestByIntent.get();
            Optional<ResponseArgument> optionalResponseArgument = historyRequest.getResponse().getResponseArgument(ResponseArgumentName.COUNT);
            if (optionalResponseArgument.isPresent()) {
                ResponseArgument responseArgument = optionalResponseArgument.get();
                return Optional.of(Integer.valueOf(responseArgument.getValue()));
            }

        }
        return Optional.empty();
    }

    private Optional<String> getOptionalArgumentValue(Request request, ArgumentName argumentName) {
        Optional<Argument> optionalArgument = request.getOptionalArgument(argumentName);
        return optionalArgument.map(argument -> StringUtils.join(argument.getArgumentValues(), ", "));
    }

    @Override
    public void destroy() {

    }

}
