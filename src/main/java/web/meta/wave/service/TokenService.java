package web.meta.wave.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.meta.wave.model.MetaToken;
import web.meta.wave.repository.TokenRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    private final CoinMarketCupService coinMarketCupService;

    public TokenService(TokenRepository tokenRepository, CoinMarketCupService coinMarketCupService) {
        this.tokenRepository = tokenRepository;
        this.coinMarketCupService = coinMarketCupService;
    }

    @Transactional(readOnly = true)
    public List<MetaToken> findAll() {
        return tokenRepository.findAll();
    }

    @Transactional
    public boolean addToken(Long code) {
        if (tokenRepository.existsByTokenCode(code)) {
            return false;
        }
        String name = coinMarketCupService.getTokenNameById(code);
        String symbol = coinMarketCupService.getTokenSymbolById(code);
        BigDecimal value = BigDecimal.valueOf(coinMarketCupService.getTokenRateById(code));
        if (name == null || symbol == null || value.equals(BigDecimal.valueOf(-1))) {
            return false;
        }
        tokenRepository.save(new MetaToken(name, symbol, code, value));
        return true;
    }

    @Transactional
    public boolean existByTokenCode(Long tokenCode) {
        return tokenRepository.existsByTokenCode(tokenCode);
    }

    @Transactional
    public MetaToken findByTokenCode(Long tokenCode) {
        return tokenRepository.findByTokenCode(tokenCode);
    }

    @Transactional
    public Optional<MetaToken> findByTokenId(Long tokenId) {
        return tokenRepository.findById(tokenId);
    }

    @Transactional
    public Long countAll(){
        return tokenRepository.count();
    }

    @Transactional
    public boolean updateToken(String name, String symbol, Long code, BigDecimal value) {
        Optional<MetaToken> token = Optional.ofNullable(findByTokenCode(code));
        if(token.isPresent()) {
            MetaToken metaToken = token.get();
            metaToken.setTokenName(name);
            metaToken.setTokenSymbol(symbol);
            metaToken.setTokenCode(code);
            metaToken.setTokenValue(value);
            tokenRepository.save(metaToken);
            return true;
        }
        return false;
    }

    @Scheduled(fixedRate = 600010)
    public void updateAllPrices() {
        List<MetaToken> all = tokenRepository.findAll();
        for (MetaToken token : all) {
            Long code = token.getTokenCode();
            String name = coinMarketCupService.getTokenNameById(code);
            String symbol = coinMarketCupService.getTokenSymbolById(code);
            BigDecimal value = BigDecimal.valueOf(coinMarketCupService.getTokenRateById(code));

            if(updateToken(name, symbol, code, value)){
                System.out.println("update token " + code);
            }
        }
    }

}
